package com.keystow.app.page;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class PageWrapper<T> {

	private final Page<T> page;

	private final UriComponentsBuilder uriBuilder;

	// @formatter:off
    public PageWrapper(Page<T> page, HttpServletRequest httpServletRequest) {
        this.page = page;
        String httpUrl = httpServletRequest.getRequestURL()
                                           .append(httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "")
                                           .toString().replaceAll("\\+", "%20")
                                           .replaceAll("excluido", "");

        this.uriBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
    }

    public List<T> getConteudo() {
        return page.getContent();
    }

    public boolean isVazia() {
        return page.getContent().isEmpty();
    }

    public int getAtual() {
        return page.getNumber();
    }

    public boolean isPrimeira() {
        return page.isFirst();
    }

    public boolean isUltima() {
        return page.isLast();
    }

    public int getTotal() {
        return page.getTotalPages();
    }

    public String urlParaPagina(int pagina) {
        return uriBuilder.replaceQueryParam("page", pagina).build(true).encode().toUriString();
    }

    public String urlOrdenada(String propriedade) {
        UriComponentsBuilder uriBuilderOrder = UriComponentsBuilder.fromUriString(uriBuilder.build(true).encode().toUriString());

        String valorSort = String.format("%s,%s", propriedade, inverterDirecao(propriedade));

        return uriBuilderOrder.replaceQueryParam("sort", valorSort).build(true).encode().toUriString();
    }

    public String inverterDirecao(String propriedade) {
        String direcao = "asc";
        page.getSort();
        Order order = page.getSort().getOrderFor(propriedade);
        if (order != null) {
            direcao = Sort.Direction.ASC.equals(order.getDirection()) ? "desc" : "asc";
        }
        return direcao;
    }

    public boolean descendente(String propriedade) {
        return inverterDirecao(propriedade).equals("asc");
    }

    public boolean ordenada(String propriedade) {
        page.getSort();
        Order order = page.getSort().getOrderFor(propriedade);

        if (order == null) {
            return false;
        }
        return page.getSort().getOrderFor(propriedade) != null;
    }

}
