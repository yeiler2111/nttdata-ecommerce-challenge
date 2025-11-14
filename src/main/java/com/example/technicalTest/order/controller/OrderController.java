package com.example.technicalTest.order.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.technicalTest.order.dto.CreateOrderRequest;
import com.example.technicalTest.order.dto.OrderResponse;
import com.example.technicalTest.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
@SecurityRequirement(name = "bearerAuth") 
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
            summary = "Crear pedido",
            description = "Crea un nuevo pedido para el usuario autenticado. "
                    + "El total se calcula automáticamente según los productos solicitados.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pedido creado correctamente",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Principal principal) {

        String email = principal.getName();
        OrderResponse response = orderService.createOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de un pedido",
            description = "Retorna el detalle de un pedido perteneciente al usuario autenticado. "
                    + "Un usuario no puede acceder a pedidos de otros usuarios.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pedido obtenido correctamente",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pedido no encontrado para este usuario",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id,
            Principal principal) {

        String email = principal.getName();
        OrderResponse response = orderService.getOrderForUser(id, email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/mis-pedidos")
    @Operation(
            summary = "Listar pedidos del usuario autenticado",
            description = "Retorna todos los pedidos asociados al usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de pedidos obtenida correctamente",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<List<OrderResponse>> myOrders(Principal principal) {

        String email = principal.getName();
        List<OrderResponse> responses = orderService.getOrdersForUser(email);
        return ResponseEntity.ok(responses);
    }
}
