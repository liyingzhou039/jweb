package com.jweb.sys.entity.identity;

import com.jweb.common.persistent.annotation.Bean;
import com.jweb.common.persistent.annotation.Field;

/**
* @ClassName: Menu
* @Description: TODO
* @author: liyz
* @date: 2018年1月31日 下午4:15:46
*/
@Bean(table="jweb_sys_menu",name="菜单")
public class Menu {

   @Field(name="ID")
   private String id;

   @Field(name="编码",required=true,validType={"length(0,10)"})
   private String code;

   @Field(name="名称",required=true,validType={"length(0,10)"})
   private String name;

   @Field(name="URL")
   private String href;

   @Field(name="图标")
   private String icon;

   @Field(name="排序")
   private int orderNum;

   @Field(name="描述",size=1000)
   private String description;

   @Field(name="全拼")
   private String qrpn;

   @Field(name="简拼")
   private String jmpn;

   @Field(name="父级编码",required=true)
   private String parentCode;

   public String getId() {
       return id;
   }

   public void setId(String id) {
       this.id = id;
   }

   public String getCode() {
       return code;
   }

   public void setCode(String code) {
       this.code = code;
   }

   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public String getHref() {
       return href;
   }

   public void setHref(String href) {
       this.href = href;
   }

   public String getIcon() {
       return icon;
   }

   public void setIcon(String icon) {
       this.icon = icon;
   }

   public int getOrderNum() {
       return orderNum;
   }

   public void setOrderNum(int orderNum) {
       this.orderNum = orderNum;
   }

   public String getDescription() {
       return description;
   }

   public void setDescription(String description) {
       this.description = description;
   }

   public String getQrpn() {
       return qrpn;
   }

   public void setQrpn(String qrpn) {
       this.qrpn = qrpn;
   }

   public String getJmpn() {
       return jmpn;
   }

   public void setJmpn(String jmpn) {
       this.jmpn = jmpn;
   }

   public String getParentCode() {
       return parentCode;
   }

   public void setParentCode(String parentCode) {
       this.parentCode = parentCode;
   }
}