package com.raizesnordeste.service;

import com.raizesnordeste.dto.ProdutoRequest;
import com.raizesnordeste.model.Categoria;
import com.raizesnordeste.model.Produto;
import com.raizesnordeste.repository.CategoriaRepository;
import com.raizesnordeste.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository repository, CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public Produto criar(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNome(request.nome);
        produto.setDescricao(request.descricao);
        produto.setPreco(request.preco);
        produto.setEstoque(request.estoque);

        if (request.categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(request.categoriaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

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
