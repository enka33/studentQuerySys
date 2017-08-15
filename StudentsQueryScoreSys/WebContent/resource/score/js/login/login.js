$(document).ready(function() {
	console.log($);
	console.log($$);
	$("#input3").val("");
	$("#input3")[0].focus();
	
	$("#input3").bind('input porpertychange',function(){//实时监听账号输入情况
		if(/\D/.test($(this).val())) {
			if($("#divIdError").hasClass("divNone")) {
				$("#divIdError").removeClass("divNone");
			}
		} else if(!/\D/.test($(this).val())) {
			if(!$("#divIdError").hasClass("divNone")) {
				$("#divIdError").addClass("divNone");
			}	
		}
	});
	$("#inputPassword3").bind('input porpertychange',function(){//实时监听密码输入情况
		if(/\W/.test($(this).val())) {
			if($("#divPasswordError").hasClass("divNone")) {
				$("#divPasswordError").removeClass("divNone");
			}
		} else if(!/\W/.test($(this).val())) {
			if(!$("#divPasswordError").hasClass("divNone")) {
				$("#divPasswordError").addClass("divNone");
			}	
		}
	});
	$("#btnLogin").click(function() {
		var userId = $("#input3").val();
		var password = $("#inputPassword3").val();
		var userPassword = $$.$hashCode(password);
		var idLen = userId.length;
		var passwordLen = password.length;
		
		if(userId == "" || password == "") {
			alert("没写全不让登录(￣へ￣)!!!");
			return;
		} else if(!$("#divIdError").hasClass("divNone") || !$("#divPasswordError").hasClass("divNone")) {
			alert("咋滴咧,还想硬闯ヽ(`Д´)ﾉ！！！");
			return;
		} else if(idLen !== 8 && idLen !== 6 && idLen !== 5) {
			alert("账号或密码错误！");
			return;
		} else if(passwordLen > 15 || passwordLen < 6) {
			alert("账号或密码错误！");
			return;
		}
		
		
		var locationObj = {
				8 : "./login/studentLogin.action?id=" + userId + "&password=" + userPassword,
				6 : "./login/teacherLogin.action?id=" + userId + "&password=" + userPassword,
				5 : ""
		};
		location = locationObj[idLen];
		$.cookie("password", userPassword);//建立cookie可供queryScore.js取得password，有可能放的是错误的password，但下个页面能取到的必然是最后一次放的正确的那个
	});
	if(location.search === "?error=error") {
		var originalLocation = location.href.substring(0, location.href.lastIndexOf("/"));
		var success = function(json) {
			alert(json["errorMess"]);
			location.href = originalLocation;
		}
		var error = function(json) {
			location = "./login.html";
			location.href = originalLocation;
		}
		$.ajax({
			url : "./check/checkLogin.action?id=error",
			type : "POST",
			async : false,
			dataType : "json",
			success : success,
			error : error
		});
	}
});
