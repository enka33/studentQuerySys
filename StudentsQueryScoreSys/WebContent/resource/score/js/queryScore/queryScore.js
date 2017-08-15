$(document).ready(function() {
	var id = location.search.substring(4);
	$.ajax({
		url : "./check/checkLogin.action?id=" + id,
		type : "POST",
		async : false,
		dataType : "json",
		success : function(json) {
			if(json["errorMess"] == '非法入侵') {
				alert("非法入侵了呢(・ω<)!");
				location = "./login.html"
				return;
			} else {
				initView(json);
			}
		},
		error : function(json) {
			alert("ajax失败了")
			location = "./login.html";
			return;
		}
	});
	
	function initView(json) {
		$("#photo")[0].src += json["id"] + ".jpg";
		$("#name").html(json["name"]);
		$("#id").html(json["id"]);
		$("#department").html(json["department"]);
		$("#major").html(json["major"]);
		var date = new Date();
		var year = date.getFullYear();
		var month = date.getMonth()+1;
		//2月分年份，8月分学期
		var startYear = "20" + json["id"].substring(2, 4);
		var startYearInt = parseInt(startYear);
		var yearStr = "";
		for(var i = startYearInt; i < year; i++) {
			var temp = i + 1;
			if(i == year-1) {
				if(month >= 2) {
					yearStr += "<option>" + i + "-" + temp + "学年</option>";
				}
			} else {
				yearStr += "<option>" + i + "-" + temp + "学年</option>"
			}
		}
		
		$("#selectYear").append(yearStr);
		$("#selectYear").click(function() {
			$("#selectTerm").empty(); 
			var termStr = "";
			var selectYear = $("#selectYear").val();
			if(selectYear.length > 2) {
				if(selectYear.indexOf(String(year)) == -1) {
					termStr = "<option>全部</option><option>第1学期</option><option>第2学期</option>";
				} else {
					termStr = month > 8 ? "<option>全部</option><option>第1学期</option><option>第2学期</option>" : "<option>全部</option><option>第1学期</option>";
				}
			} else {
				termStr = "<option>全部</option>";
			}
			
			$("#selectTerm").append(termStr);
		});
	}
	
	$(".btn.btn-success.top30.floatRight").click(function() {//查询成绩
		var queryId = this.id;
		var queryContent = "";
		if(queryId == "btnScoreQuery") {
			queryContent = "all"
		} else if(queryId == "btnFailScoreQuery") {
			queryContent = "nopass"
		}
		$.ajax({
			url : "./queryScore.action",
			type : "POST",
			data : {
				id : id,
				queryContent : queryContent,
				year : $("#selectYear").val(),
				term : $("#selectTerm").val()
			},
			async : true,
			dataType : "json",
			success : scoreText,
			error : function(json) {console.log("error", json);}
		});
	});
	var scoreText = function(json) {
		$("tbody tr").remove();
		var appendStr = "";
		$.each(json, function(name,value) {
			console.log(name);
			console.log(value);
			appendStr += "<tr><td>" + value["subject"] + "</td><td>" + value["score"] + "</td>";
			if(value["makeupScore"] == '0') {
				appendStr += "<td>无</td><td>0</td></tr>";
			} else {
				appendStr += "<td>" + value["makeupScore"] + "</td><td>" + value["status"] + "</td></tr>"; 
			}
		});
		$("tbody").append(appendStr);
	}
	
	$("#btnExitScoreQuery").click(function() {//退出按钮
		var r=confirm("确定要退出吗(｀・ω・´)?");
		if (r==true) {
		  location = "./login.html";
		}
	});
	
	$("#btnModifyPDW").click(function() {//修改密码
		var pastPassword = $.cookie('password');
		var pastPWD = $("#iptPastPWD").val();
		var confirmPWD = $("#iptConfirmPWD").val();
		var nowPWD = $("#iptNowPWD").val();
		if(6 < nowPWD.length < 15 && nowPWD == confirmPWD && pastPassword == $$.$hashCode(pastPWD)) {
			var hashPWD = $$.$hashCode(nowPWD);
			$.ajax({
				url : "./modifyPassword.action?id="+ id + "&pwd=" + hashPWD,
				type : "GET",
				async : false,
				dataType : "text",
				success : function(text) {
					$.cookie('password', hashPWD);
					alert("恭喜您，修改成功");
					
					$("#modalClose").click();
				},
				error : function(text) {
					alert("修改密码的ajax请求失败!");
					return;
				}
			});
		} else {
			alert("请核查您的填写信息!(＾＿－)");
		}
	});
	
	$("#modalModify").click(function() {//初始化模态框
		$("#iptPastPWD").val("");
		$("#iptPastPWD")[0].focus();
		$("#iptNowPWD").val("");
		$("#iptConfirmPWD").val("");
	});
	
	$("#logout").click(function() {
		var r=confirm("确定要登出吗(｀・ω・´)?");
		if (r==true) {
			location = "./logout.action?id=" + id;
		}
	});
});