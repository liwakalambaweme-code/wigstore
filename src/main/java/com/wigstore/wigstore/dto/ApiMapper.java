package com.wigstore.wigstore.dto;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.service.ExchangeRateService;
import com.wigstore.wigstore.service.WigImageService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApiMapper {

    private final WigImageService imageService;
    private final ExchangeRateService exchangeRateService;

    public ApiMapper(WigImageService imageService, ExchangeRateService exchangeRateService) {
        this.imageService = imageService;
        this.exchangeRateService = exchangeRateService;
    }

    public UserResponse toUser(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhoneNumber(),
                u.getRole(), u.getPreferredCurrency(), u.getTheme(), u.isEmailConsent());
    }

    public ImageResponse toImage(WigImage i) {
        return new ImageResponse(i.getId(), imageService.urlFor(i),
                i.isPrimaryImage(), i.getDisplayOrder());
    }

    public WigResponse toWig(Wig w, Currency currency) {
        Currency shown = (currency == null) ? Currency.USD : currency;
        List<ImageResponse> images = imageService.getImages(w.getId()).stream()
                .map(this::toImage).toList();
        BigDecimal displayPrice = exchangeRateService.convert(w.getPriceUsd(), shown);
        return new WigResponse(w.getId(), w.getName(), w.getDescription(),
                w.getPriceUsd(), displayPrice, shown, w.getColor(),
                w.getLengthInches(), w.getDensityPercent(), w.getHairType(),
                w.getStockQuantity(), w.isActive(), coverUrl(images), images);
    }

    public CartItemResponse toCartItem(CartItem c) {
        Wig w = c.getWig();
        List<ImageResponse> images = imageService.getImages(w.getId()).stream()
                .map(this::toImage).toList();
        return new CartItemResponse(w.getId(), w.getName(), w.getPriceUsd(),
                c.getQuantity(), coverUrl(images));
    }

    public OrderResponse toOrder(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(i.getWig().getId(), i.getWig().getName(),
                        i.getQuantity(), i.getUnitPriceUsd()))
                .toList();
        return new OrderResponse(o.getId(), o.getStatus(), o.getTotalUsd(), o.getCreatedAt(),
                o.getDeliveryAddress(), o.getCity(), o.getDeliveryNotes(),
                o.getCustomer().getName(), items);
    }

    public PaymentResponse toPayment(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrder().getId(), p.getMethod(),
                p.getStatus(), p.getAmountUsd(), p.getCurrencyPaid(), p.getAmountPaid(),
                p.getTransactionReference(), p.getCreatedAt(), p.getPaidAt());
    }

    public NotificationResponse toNotification(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getMessage(),
                n.isSeen(), n.getCreatedAt());
    }

    // the cover is the image marked primary, or the first one if none is marked
    private String coverUrl(List<ImageResponse> images) {
        return images.stream().filter(ImageResponse::primaryImage).findFirst()
                .or(() -> images.stream().findFirst())
                .map(ImageResponse::url)
                .orElse(null);
    }
}