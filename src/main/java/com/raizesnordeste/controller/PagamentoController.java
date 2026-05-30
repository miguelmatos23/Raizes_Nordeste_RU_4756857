package com.raizesnordeste.controller;

import com.raizesnordeste.dto.PagamentoRequest;
import com.raizesnordeste.model.Pagamento;
import com.raizesnordeste.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamento")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/{pedidoId}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'CLIENTE')")
    @Operation(summary = "Efetua o pagamento de um pedido")
    public ResponseEntity<Pagamento> pagar(
            @PathVariable UUID pedidoId,
            @RequestBody PagamentoRequest request) {
        Pagamento pagamento = service.efetuarPagamento(pedidoId, request.formaPagamento);
        return ResponseEntity.ok(pagamento);
    }
}
