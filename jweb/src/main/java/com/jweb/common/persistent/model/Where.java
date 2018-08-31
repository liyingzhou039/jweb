package com.jweb.common.persistent.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jweb.common.util.JsonUtil;
import com.jweb.common.util.StringUtil;

public class Where {
	private boolean isSub=false;
	private Condition condition;
	private Where next;
	private String relation;
	private Where sub;
	private Where() {
		this.isSub = true;
	}
	private Where(String name,Expression ex,Object value) {
		this.condition =new Condition(name,ex,value);
		this.isSub = false;
	}
	public static Where create(){
		return new Where();
	}
	public static Where create(String name,Expression ex,Object value) {
		return new Where(name,ex,value);
	}
	
	public Where and(String name,Expression ex,Object value) {
		Where where = new Where(name,ex,value);
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next = where;
		where.relation="and";
		return this;
	}
	public Where or(String name,Expression ex,Object value) {
		Where where = new Where(name,ex,value);
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next = where;
		where.relation="or";
		return this;
	}
	public Where sub(Where where) {
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next= new Where();
		next.next.sub=where;
		return this;
	}
	public Where andSub(Where where) {
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next= new Where();
		next.next.sub=where;
		next.next.relation="and";
		return this;
	}
	public Where orSub(Where where) {
		Where next=this;
		while(next.next!=null) {
			next=next.next;
		}
		next.next= new Where();
		next.next.sub=where;
		next.next.relation="or";
		return this;
	}
	@Override
	public String toString() {
		PrepareSql prepareSql = toSqlAndParams(this,new PrepareSql());
		
		return prepareSql.toString();
	}
	public static PrepareSql toSqlAndParams(Where where,PrepareSql prepareSql) {
		if(where.isSub) {
			if(null!=where.sub) {
				String relation = where.relation;
				if(relation==null){
					relation="";
				}else{
					relation = " "+relation+" ";
				}
				prepareSql.setSql(prepareSql.getSql() + relation + "(");
				toSqlAndParams(where.sub, prepareSql);
				prepareSql.setSql(prepareSql.getSql() + ")");
			}
			if(null!=where.next) {
				toSqlAndParams(where.next, prepareSql);
			}
		}else {
			String sql = where.condition.getExpression().value();
			sql = sql.replace("${name}", StringUtil.toSlide(where.condition.getName()));

			if (where.condition.getValue() instanceof List) {
				List<?> values = (List<?>) where.condition.getValue();
				String vs = "";
				for (int i = 0; i < values.size(); i++) {
					vs += ",?";
				}
				sql = sql.replace("${value}", vs);
				prepareSql.getParams().addAll(values);
			} else if (where.condition.getValue() != null && where.condition.getValue().getClass().isArray()) {
				Object[] os = (Object[]) where.condition.getValue();
				String vs = "";
				for (int i = 0; i < os.length; i++) {
					vs += ",?";
					prepareSql.getParams().add(os[i]);
				}
				sql = sql.replace("${value}", vs);
			} else {
				if (where.condition.getValue() == null) {
					if (where.condition.getExpression().value().contains("!="))
						sql = StringUtil.toSlide(where.condition.getName()) + " is not null ";
					else
						sql = StringUtil.toSlide(where.condition.getName()) + " is null ";
				} else {
					sql = sql.replace("${value}", "?");
					prepareSql.getParams().add(where.condition.getValue());
				}
			}
			String relation = where.relation;
			if(relation==null){
				relation="";
			}else{
				relation = " "+relation+" ";
			}
			prepareSql.setSql(prepareSql.getSql()+relation+sql);
			if(null!=where.next) {
				toSqlAndParams(where.next,prepareSql);
			}
		}
		return prepareSql;
	}

	public static Where parse(String exString){
		List<String> exs = new ArrayList<>();
		int curIndex=0;
		for(int i=0;i<exString.length()-1;i++){
			if(!exString.substring(curIndex).contains("&&") && !exString.substring(curIndex).contains("||")){
				String tex = exString.substring(curIndex).trim();
				if(tex.startsWith("(")){
					while(tex.startsWith("("))
					{
						exs.add("(");
						tex=tex.substring(1).trim();
					}
					exs.add(tex);
				}else if(tex.endsWith(")")){
					List<String> ends = new ArrayList<>();
					while(tex.endsWith(")")){
						tex= tex.substring(0,tex.length()-1).trim();
						ends.add(")");
					}
					exs.add(tex);
					exs.addAll(ends);
				}else{
					exs.add(tex);
				}
				break;
			}
			//&&
			if(exString.charAt(i)=='&' && exString.charAt(i+1)=='&'){
				String tex = exString.substring(curIndex,i).trim();
				if(tex.startsWith("(")){
					while(tex.startsWith("("))
					{
						exs.add("(");
						tex=tex.substring(1).trim();
					}
					exs.add(tex);
				}else if(tex.endsWith(")")){
					List<String> ends = new ArrayList<>();
					while(tex.endsWith(")")){
						tex= tex.substring(0,tex.length()-1).trim();
						ends.add(")");
					}
					exs.add(tex);
					exs.addAll(ends);
				}else{
					exs.add(tex);
				}
				exs.add(exString.substring(i,i+2));
				i++;
				curIndex=i+1;
			}
			//||
			if(exString.charAt(i)=='|' && exString.charAt(i+1)=='|'){
				String tex = exString.substring(curIndex,i).trim();
				if(tex.startsWith("(")){
					while(tex.startsWith("("))
					{
						exs.add("(");
						tex=tex.substring(1).trim();
					}
					exs.add(tex);
				}else if(tex.endsWith(")")){
					List<String> ends = new ArrayList<>();
					while(tex.endsWith(")")){
						tex= tex.substring(0,tex.length()-1).trim();
						ends.add(")");
					}
					exs.add(tex);
					exs.addAll(ends);
				}else{
					exs.add(tex);
				}
				exs.add(exString.substring(i,i+2));
				i++;
				curIndex=i+1;
			}
		}

		return parse(exs);
	}
	public static Where parse(List<String> exs){
		Where where = null;
		for(int i=0;i<exs.size();i++){
			String ex = exs.get(i);
			ex = ex.trim();
			if("&&".equals(ex) && (i+1)<exs.size()){
				String nextEx = exs.get(i+1).trim();
				if("(".equals(nextEx)){
					int level = 0;
					List<String> subExs = new ArrayList<>();
					for(int j = i+1;j<exs.size();j++){
						if(exs.get(j).equals("(")) level++;
						if(exs.get(j).equals(")")) level--;
						subExs.add(exs.get(j));
						if(level==0){
							i=j;
							break;
						}
					}
					where.andSub(parse(subExs));
				}else{
					Map<String,Object> exParam = parseExpression(nextEx);
					where.and((String)exParam.get("name")
							,(Expression) exParam.get("expression")
							,exParam.get("value"));
					i++;
				}
			}else if("||".equals(ex) && (i+1)<exs.size()){
				String nextEx = exs.get(i+1).trim();
				if("(".equals(nextEx)){
					int level = 0;
					List<String> subExs = new ArrayList<>();
					for(int j = i+1;j<exs.size();j++){
						if(exs.get(j).equals("(")) level++;
						if(exs.get(j).equals(")")) level--;
						subExs.add(exs.get(j));
						if(level==0){
							i=j;
							break;
						}
					}
					where.orSub(parse(subExs));
				}else{
					Map<String,Object> exParam = parseExpression(nextEx);
					where.or((String)exParam.get("name")
							,(Expression) exParam.get("expression")
							,exParam.get("value"));
					i++;
				}
			}else if(!ex.equals("(") && !ex.equals(")")){
				Map<String,Object> exParam = parseExpression(ex);
				where = Where.create((String) exParam.get("name"), (Expression) exParam.get("expression")
							, exParam.get("value"));
			}else if(ex.equals("(") && (i+1)<exs.size()){
				String nextEx = exs.get(i+1).trim();
				if("(".equals(nextEx)) {
					int level = 0;
					List<String> subExs = new ArrayList<>();
					for (int j = i + 1; j < exs.size(); j++) {
						if (exs.get(j).equals("(")) level++;
						if (exs.get(j).equals(")")) level--;
						subExs.add(exs.get(j));
						if (level == 0) {
							i = j;
							break;
						}
					}
					if (null == where) {
						where = Where.create();
					}
					where.sub(parse(subExs));
				}else {
					Map<String,Object> exParam = parseExpression(nextEx);
					where=Where.create((String)exParam.get("name")
							,(Expression) exParam.get("expression")
							,exParam.get("value"));
					i++;
				}

			}
		}
		return where;
	}
	private static Map<String,Object> parseExpression(String exString){
		String[] es = exString.split("[=<>]");
		if(exString.indexOf("in[")!=-1){
			es = exString.split("in\\[");
			es[1] = "["+es[1];
		}
		if(es.length!=2){
			es = new String[]{es[0],""};
		}
		es[0] = es[0].trim();
		es[1] = es[1].trim();
		if(es[1].startsWith("'")) es[1] = es[1].substring(1);
		if(es[1].startsWith("\"")) es[1] = es[1].substring(1);
		if(es[1].endsWith("'")) es[1] = es[1].substring(0,es[1].length()-1);
		if(es[1].endsWith("\"")) es[1] = es[1].substring(0,es[1].length()-1);
		Map<String,Object> ex = new HashMap<>();
		ex.put("name",es[0]);
		if(exString.indexOf("=")!=-1){
			if(es[1].indexOf("%")!=-1){
				ex.put("expression",Expression.like);
			}else{
				ex.put("expression",Expression.eq);
			}
			ex.put("value",es[1]);

		}else if(exString.indexOf("in[")!=-1){
			ex.put("expression",Expression.in);
			ex.put("value",JsonUtil.jsonToBean(es[1],List.class));
		}else if(exString.indexOf("<")!=-1){
			ex.put("expression",Expression.lt);
			ex.put("value",es[1]);
		}else if(exString.indexOf(">")!=-1){
			ex.put("expression",Expression.gt);
			ex.put("value",es[1]);
		}
		return ex;
	}
}
