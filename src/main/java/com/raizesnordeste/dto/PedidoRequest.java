package com.raizesnordeste.dto;

import java.util.List;
import java.util.UUID;

public class PedidoRequest {

    public UUID clienteId;
    public String canalPedido;
    public List<ItemRequest> itens;

    public static class ItemRequest {
        public UUID produtoId;
        public Integer quantidade;
    }
}
