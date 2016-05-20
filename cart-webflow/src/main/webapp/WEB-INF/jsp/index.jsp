<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Cart Application</title>
</head>
  <body>
	<h1>Hello!</h1>
	<br />
	<a href="shopping.do">View Cart</a>
	<p>
	这几个页面都使用了变量 flowExecutionUrl ，表示 flow 执行到当前状态时的 URL 。 flowExecutionUrl 的值已经由 Spring Web Flow 2.0 框架的代码进行赋值，并放入相应的 model 中供 view 访问。
	flowExecutionUrl 的值包含 flow 在执行过程中会为每一状态生成的唯一的 key ，因此不可用其他手段来获取。请求参数中 _eventId 的值与shoppting.xml中 transition 元素的 on 属性的值是对应的，
	在接收到_eventId参数后，相应transition会被执行。
	</p>
  </body>
</html>