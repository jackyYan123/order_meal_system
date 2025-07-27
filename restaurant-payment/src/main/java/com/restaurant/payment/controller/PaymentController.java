package com.restaurant.payment.controller;

import com.restaurant.common.result.Result;
import com.restaurant.payment.dto.CreatePaymentRequest;
import com.restaurant.payment.dto.PaymentDto;
import com.restaurant.payment.service.AlipayService;
import com.restaurant.payment.service.PaymentService;
import com.restaurant.payment.service.WechatPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final WechatPayService wechatPayService;
    private final AlipayService alipayService;

    /**
     * 创建支付
     */
    @PostMapping
    public Result<PaymentDto> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        log.info("创建支付请求: {}", request);
        PaymentDto payment = paymentService.createPayment(request);
        return Result.success(payment);
    }

    /**
     * 处理支付
     */
    @PostMapping("/{paymentId}/process")
    public Result<Map<String, Object>> processPayment(@PathVariable Long paymentId) {
        Map<String, Object> result = paymentService.processPayment(paymentId);
        return Result.success(result);
    }

    /**
     * 查询支付详情
     */
    @GetMapping("/{paymentId}")
    public Result<PaymentDto> getPaymentById(@PathVariable Long paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return Result.success(payment);
    }

    /**
     * 根据支付流水号查询支付详情
     */
    @GetMapping("/paymentNo/{paymentNo}")
    public Result<PaymentDto> getPaymentByPaymentNo(@PathVariable String paymentNo) {
        PaymentDto payment = paymentService.getPaymentByPaymentNo(paymentNo);
        return Result.success(payment);
    }

    /**
     * 根据订单号查询支付记录
     */
    @GetMapping("/order/{orderNo}")
    public Result<List<PaymentDto>> getPaymentsByOrderId(@PathVariable String orderNo) {
        List<PaymentDto> payments = paymentService.getPaymentsByOrderId(orderNo);
        return Result.success(payments);
    }

    /**
     * 申请退款
     */
    @PostMapping("/{paymentId}/refund")
    public Result<Void> refund(@PathVariable Long paymentId, @RequestParam String reason) {
        paymentService.refund(paymentId, reason);
        return Result.success();
    }

    /**
     * 现金支付确认
     */
    @PostMapping("/{paymentId}/confirm-cash")
    public Result<Void> confirmCashPayment(@PathVariable Long paymentId) {
        paymentService.confirmCashPayment(paymentId);
        return Result.success();
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/callback")
    public String wechatPayCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到微信支付回调: {}", callbackData);
        try {
            wechatPayService.handlePaymentCallback(callbackData);
            return "SUCCESS";
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "FAIL";
        }
    }

    /**
     * 支付宝支付回调
     */
    @PostMapping("/alipay/callback")
    public String alipayCallback(@RequestParam Map<String, Object> callbackData) {
        log.info("收到支付宝支付回调: {}", callbackData);
        try {
            alipayService.handlePaymentCallback(callbackData);
            return "success";
        } catch (Exception e) {
            log.error("处理支付宝支付回调失败", e);
            return "fail";
        }
    }
}