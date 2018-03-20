package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public class Goods {
  private Long id;

  private Long price;

  private Integer remaining;

  public Long getId() {
    return id;
  }

  public Goods setId(Long id) {
    this.id = id;
    return this;
  }

  public Long getPrice() {
    return price;
  }

  public Goods setPrice(Long price) {
    this.price = price;
    return this;
  }

  public Integer getRemaining() {
    return remaining;
  }

  public Goods setRemaining(Integer remaining) {
    this.remaining = remaining;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Goods{");
    sb.append("id=").append(id);
    sb.append(", price=").append(price);
    sb.append(", remaining=").append(remaining);
    sb.append('}');
    return sb.toString();
  }
}
