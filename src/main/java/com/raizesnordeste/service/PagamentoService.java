package com.raizesnordeste.service;

import com.raizesnordeste.model.*;
import com.raizesnordeste.repository.PagamentoRepository;
import com.raizesnordeste.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pagamento efetuarPagamento(UUID pedidoId, String formaPagamento) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedido.getStatusPedido() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pedido não está aguardando pagamento");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
        pagamento.setStatusPagamento(StatusPagamento.APROVADO);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setCodigoTransacao(UUID.randomUUID().toString());

        pedido.setStatusPedido(StatusPedido.PAGO);
        pedidoRepository.save(pedido);

        return pagamentoRepository.save(pagamento);
    }
}
