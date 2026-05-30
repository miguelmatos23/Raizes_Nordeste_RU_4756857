package com.raizesnordeste.controller;

import com.raizesnordeste.dto.PedidoRequest;
import com.raizesnordeste.model.Pedido;
import com.raizesnordeste.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
@Tag(name = "Pedido")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping("/criar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'CLIENTE')")
    @Operation(summary = "Cria um novo pedido")
    public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest request) {
        Pedido pedido = service.criarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Lista pedidos com filtro opcional por canal")
    public ResponseEntity<List<Pedido>> listar(
            @RequestParam(required = false) String canalPedido) {
        return ResponseEntity.ok(service.listarPedidos(canalPedido));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Consulta pedido por ID")
    public ResponseEntity<Pedido> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/preparar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Altera status para EM_PREPARO")
    public ResponseEntity<Pedido> preparar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.prepararPedido(id));
    }

    @PatchMapping("/{id}/pronto")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Altera status para AGUARDANDO_ENTREGADOR")
    public ResponseEntity<Pedido> pronto(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarPronto(id));
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Altera status para SAIU_PARA_ENTREGA")
    public ResponseEntity<Pedido> entregar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.enviarParaEntrega(id));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Altera status para ENTREGUE")
    public ResponseEntity<Pedido> finalizar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.finalizarEntrega(id));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @Operation(summary = "Cancela o pedido e devolve estoque")
    public ResponseEntity<Pedido> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelarPedido(id));
    }
}
