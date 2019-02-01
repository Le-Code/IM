<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	request.setAttribute("path",request.getContextPath());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${path}/js/jquery.js"></script>
<title>web</title>
</head>
<script type="text/javascript">
	$(function() {
		$("span").click(function(){
			var uname = $("input").val();
			if(uname == ""){
				alert("请先输入用户名");
			}else {
				var roomid = $(this).html()
				location.href="${path}/home/room.do?uname="+uname+"&roomid="+roomid;
			}
		})
	})
</script>
<style>
	span:HOVER{
		color: red;
	}
	span{
		cursor:pointer;
	}
</style>
<body>
	用户名:<input type="text">   /*注：请先输入用户名，且保证用户名唯一，再点击下面的房间加入房间
	<h1><span>room1</span></h1>
	<h1><span>room2</span></h1>
</body>
</html>