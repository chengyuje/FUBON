package com.systex.jbranch.app.server.fps.fps230;

import java.util.List;

import com.systex.jbranch.app.server.fps.fps200.FPS200InputVO;

public class FPS230InputVO extends FPS200InputVO {
  public FPS230InputVO() {
  }

  private List<FPS230ProdInputVO> prodList;

  public List<FPS230ProdInputVO> getProdList() {
    return prodList;
  }

  public void setProdList(List<FPS230ProdInputVO> prodList) {
    this.prodList = prodList;
  }

}
