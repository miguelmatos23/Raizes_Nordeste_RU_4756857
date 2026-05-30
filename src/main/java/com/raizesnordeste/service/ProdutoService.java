package com.raizesnordeste.service;

import com.raizesnordeste.dto.ProdutoRequest;
import com.raizesnordeste.model.Produto;
import com.raizesnordeste.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto criar(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNome(request.nome);
        produto.setPreco(request.preco);
        produto.setEstoque(request.estoque);

        return repository.save(produto);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }
}
