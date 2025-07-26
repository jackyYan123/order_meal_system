package com.restaurant.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.payment.dto.CreatePaymentRequest;
import com.restaurant.payment.dto.PaymentDto;
import com.restaurant.payment.entity.Payment;
import com.restaurant.payment.enums.PaymentMethod;
import com.restaurant.payment.enums.PaymentStatus;
import com.restaurant.payment.mapper.PaymentMapper;
import com.restaurant.payment.service.PaymentService;
import com.restaurant.payment.service.WechatPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentMapper paymentMapper;
    private final WechatPayService wechatPayService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentDto createPayment(CreatePaymentRequest request) {
        log.info("创建支付记录，订单ID: {}, 金额: {}", request.getOrderId(), request.getAmount());
        
        // 检查订单是否已有成功的支付记录
        List<Payment> existingPayments = paymentMapper.selectByOrderId(request.getOrderId());
        boolean hasSuccessPayment = existingPayments.stream()
                .anyMatch(p -> PaymentStatus.SUCCESS.getCode().equals(p.getStatus()));
        
        if (hasSuccessPayment) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "订单已支付，不能重复支付");
        }
        
        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING.getCode());
        payment.setRemarks(request.getRemarks());
        
        paymentMapper.insert(payment);
        
        log.info("支付记录创建成功，支付流水号: {}", payment.getPaymentNo());
        return convertToDto(payment);
    }
    
    @Override
    public Map<String, Object> processPayment(Long paymentId) {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        
        if (!PaymentStatus.PENDING.getCode().equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "支付状态不正确");
        }
        
        PaymentMethod method = PaymentMethod.fromCode(payment.getPaymentMethod());
        Map<String, Object> result = new HashMap<>();
        
        switch (method) {
            case WECHAT:
                result = wechatPayService.createPayment(payment);
                break;
            case ALIPAY:
                // TODO: 集成支付宝支付
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "暂不支持支付宝支付");
            case CASH:
                // 现金支付直接返回成功，需要人工确认
                result.put("paymentMethod", "CASH");
                result.put("message", "请确认现金支付");
                break;
            default:
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不支持的支付方式");
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentSuccess(String paymentNo, String thirdPartyTransactionId) {
        log.info("处理支付成功回调，支付流水号: {}, 第三方交易号: {}", paymentNo, thirdPartyTransactionId);
        
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            log.error("支付记录不存在，支付流水号: {}", paymentNo);
            return;
        }
        
        payment.setStatus(PaymentStatus.SUCCESS.getCode());
        payment.setThirdPartyTransactionId(thirdPartyTransactionId);
        payment.setPaidTime(LocalDateTime.now());
        
        paymentMapper.updateById(payment);
        
        // TODO: 通知订单服务更新订单支付状态
        log.info("支付成功处理完成，支付流水号: {}", paymentNo);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentFailure(String paymentNo, String failureReason) {
        log.info("处理支付失败回调，支付流水号: {}, 失败原因: {}", paymentNo, failureReason);
        
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            log.error("支付记录不存在，支付流水号: {}", paymentNo);
            return;
        }
        
        payment.setStatus(PaymentStatus.FAILED.getCode());
        payment.setFailureReason(failureReason);
        
        paymentMapper.updateById(payment);
        
        log.info("支付失败处理完成，支付流水号: {}", paymentNo);
    }
    
    @Override
    public PaymentDto getPaymentById(Long paymentId) {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        return convertToDto(payment);
    }
    
    @Override
    public PaymentDto getPaymentByPaymentNo(String paymentNo) {
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        return convertToDto(payment);
    }
    
    @Override
    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentMapper.selectByOrderId(orderId);
        return payments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long paymentId, String reason) {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        
        if (!PaymentStatus.SUCCESS.getCode().equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有支付成功的记录才能退款");
        }
        
        PaymentMethod method = PaymentMethod.fromCode(payment.getPaymentMethod());
        
        switch (method) {
            case WECHAT:
                wechatPayService.refund(payment, reason);
                break;
            case ALIPAY:
                // TODO: 支付宝退款
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "暂不支持支付宝退款");
            case CASH:
                // 现金退款直接更新状态
                payment.setStatus(PaymentStatus.REFUNDED.getCode());
                payment.setRefundTime(LocalDateTime.now());
                payment.setRefundAmount(payment.getAmount());
                paymentMapper.updateById(payment);
                break;
            default:
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不支持的退款方式");
        }
        
        log.info("退款处理完成，支付流水号: {}", payment.getPaymentNo());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmCashPayment(Long paymentId) {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        
        if (!PaymentMethod.CASH.getCode().equals(payment.getPaymentMethod())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有现金支付才需要确认");
        }
        
        if (!PaymentStatus.PENDING.getCode().equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "支付状态不正确");
        }
        
        payment.setStatus(PaymentStatus.SUCCESS.getCode());
        payment.setPaidTime(LocalDateTime.now());
        
        paymentMapper.updateById(payment);
        
        log.info("现金支付确认完成，支付流水号: {}", payment.getPaymentNo());
    }
    
    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000));
        return "PAY" + timestamp + String.format("%03d", Integer.parseInt(random));
    }
    
    /**
     * 转换为DTO
     */
    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        BeanUtils.copyProperties(payment, dto);
        
        // 设置描述信息
        try {
            PaymentMethod method = PaymentMethod.fromCode(payment.getPaymentMethod());
            dto.setPaymentMethodDescription(method.getDescription());
        } catch (Exception e) {
            dto.setPaymentMethodDescription("未知");
        }
        
        try {
            PaymentStatus status = PaymentStatus.fromCode(payment.getStatus());
            dto.setStatusDescription(status.getDescription());
        } catch (Exception e) {
            dto.setStatusDescription("未知");
        }
        
        return dto;
    }
}