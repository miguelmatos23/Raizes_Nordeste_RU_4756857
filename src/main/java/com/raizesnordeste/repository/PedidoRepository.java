package com.raizesnordeste.repository;

import com.raizesnordeste.model.CanalPedido;
import com.raizesnordeste.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
}
