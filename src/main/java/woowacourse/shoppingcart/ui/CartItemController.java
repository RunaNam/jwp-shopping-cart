package woowacourse.shoppingcart.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import woowacourse.auth.support.AuthenticationPrincipal;
import woowacourse.shoppingcart.application.CartService;
import woowacourse.shoppingcart.dto.CartItemAddRequest;
import woowacourse.shoppingcart.dto.CartItemResponse;

@RestController
@RequestMapping("/api/mycarts")
public class CartItemController {
    private final CartService cartService;

    public CartItemController(final CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(@AuthenticationPrincipal String email) {
        List<CartItemResponse> cartItemResponses = cartService.findCartsByEmail(email);
        return ResponseEntity.ok().body(cartItemResponses);
    }

    @GetMapping({"/{cartId}"})
    public ResponseEntity<CartItemResponse> getCartItem(@AuthenticationPrincipal String email,
                                                        @PathVariable final Long cartId) {
        CartItemResponse cartItemResponse = cartService.findCart(cartId);
        return ResponseEntity.ok().body(cartItemResponse);
    }

    @PostMapping
    public ResponseEntity<Void> addCartItem(@RequestBody final CartItemAddRequest cartItemAddRequest,
                                            @AuthenticationPrincipal String email) {
        final Long cartId = cartService.addCart(cartItemAddRequest.getProductId(), cartItemAddRequest.getQuantity(),
                email);
        final URI responseLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cartId}")
                .buildAndExpand(cartId)
                .toUri();
        return ResponseEntity.created(responseLocation).build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCartItem(@AuthenticationPrincipal String email,
                                               @PathVariable final Long cartId) {
        cartService.deleteCart(email, cartId);
        return ResponseEntity.noContent().build();
    }
}
