package com.raizesnordeste.service;

import com.raizesnordeste.dto.PedidoRequest;
import com.raizesnordeste.model.*;
import com.raizesnordeste.repository.PedidoRepository;
import com.raizesnordeste.repository.ProdutoRepository;
import com.raizesnordeste.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Pedido criarPedido(PedidoRequest request) {
        Usuario cliente = usuarioRepository.findById(request.clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nÃ£o encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setCanalPedido(CanalPedido.valueOf(request.canalPedido));
        pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);

        List<ItemPedido> itens = new ArrayList<>();
        double valorTotal = 0;

        for (PedidoRequest.ItemRequest itemReq : request.itens) {
            Produto produto = produtoRepository.findById(itemReq.produtoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nÃ£o encontrado"));

            if (produto.getEstoque() < itemReq.quantidade) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Estoque insuficiente para: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - itemReq.quantidade);
            produtoRepository.save(produto);

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemReq.quantidade);
            item.setPrecoUnitario(produto.getPreco());

            valorTotal += produto.getPreco() * itemReq.quantidade;
            itens.add(item);
        }

        pedido.setItens(itens);
        pedido.setValorTotal(valorTotal);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos(String canalPedido) {
        if (canalPedido != null && !canalPedido.isBlank()) {
            return pedidoRepository.findByCanalPedido(CanalPedido.valueOf(canalPedido));
        }
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nÃ£o encontrado"));
    }

    public Pedido prepararPedido(UUID id) {
        Pedido pedido = buscarPorId(id);
        validarTransicao(pedido, StatusPedido.PAGO, "Pedido precisa estar pago");
        pedido.setStatusPedido(StatusPedido.EM_PREPARO);
        return pedidoRepository.save(pedido);
    }

    public Pedido marcarPronto(UUID id) {
        Pedido pedido = buscarPorId(id);
        validarTransicao(pedido, StatusPedido.EM_PREPARO, "Pedido ainda nÃ£o estÃ¡ pronto");
        pedido.setStatusPedido(StatusPedido.AGUARDANDO_ENTREGADOR);
        return pedidoRepository.save(pedido);
    }

    public Pedido enviarParaEntrega(UUID id) {
        Pedido pedido = buscarPorId(id);
        validarTransicao(pedido, StatusPedido.AGUARDANDO_ENTREGADOR, "Pedido nÃ£o estÃ¡ aguardando entregador");
        pedido.setStatusPedido(StatusPedido.SAIU_PARA_ENTREGA);
        return pedidoRepository.save(pedido);
    }

    public Pedido finalizarEntrega(UUID id) {
        Pedido pedido = buscarPorId(id);
        validarTransicao(pedido, StatusPedido.SAIU_PARA_ENTREGA, "Pedido ainda nÃ£o saiu para entrega");
        pedido.setStatusPedido(StatusPedido.ENTREGUE);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(UUID id) {
        Pedido pedido = buscarPorId(id);

        if (pedido.getStatusPedido() == StatusPedido.ENTREGUE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Pedido jÃ¡ entregue, entre em contato com a loja");
        }
        if (pedido.getStatusPedido() == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pedido jÃ¡ estÃ¡ cancelado");
        }

        // devolve o estoque de cada item
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatusPedido(StatusPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    private void validarTransicao(Pedido pedido, StatusPedido statusEsperado, String mensagem) {
        if (pedido.getStatusPedido() != statusEsperado) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
        }
    }
}
