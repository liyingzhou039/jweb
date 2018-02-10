package com.jweb.system.dto;

import java.util.List;

import com.jweb.system.entity.Element;
import com.jweb.system.entity.Menu;

public class PowerDto {
	private List<Menu> menus;
	private List<Element> elements;
	public List<Menu> getMenus() {
		return menus;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	public List<Element> getElements() {
		return elements;
	}
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
}
