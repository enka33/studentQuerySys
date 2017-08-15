$(document).ready(function() {
	var id = location.search.substring(4);
	var initJson = {};
	$.ajax({
		url : "./check/checkLogin.action?id=" + id,
		type : "POST",
		async : false,
		dataType : "json",
		success : function(json) {
			console.log(json);
			initJson = json;
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
	
	function initView(json) {//初始化页面
		$("#photo")[0].src += json["id"] + ".jpg";
		$("#name").html(json["name"]);
		$("#id").html(json["id"]);
		$("#department").html(json["department"]);
		
		var initMajorSelect = function(id) {//初始化专业框
			$(id).empty();
			var majorStr = "";
			majorArr = json["majorList"];
			for(var j = 0; j < majorArr.length; j++) {
				majorStr += "<option>" + majorArr[j] + "</option>";
			}
			$(id).append(majorStr);
		};
		var initGradeSelect = function(id) {//初始化年级选择框
			$(id).empty();
			var date = new Date();
			var year = date.getFullYear();
			var month = date.getMonth()+1;
			var gradeStr = "";
			for(var i = year - 4; i < year; i++) {
				i == year && year < 8 ?  gradeStr += "" : gradeStr += "<option>" + i + "级</option>";
			}
			$(id).append(gradeStr);
		};
		
		$("#selectMajor3").click(function() {//专业选择
			$("#selectSubject3").empty();
			var major = $("#selectMajor3").val();
			var subjectArr = json["majorSubjectMap"][major.substring(0, major.indexOf("("))];
			var subject = "";
			for(var i = 0; i < subjectArr.length; i++) {
				subject += "<option>" + subjectArr[i] + "</option>"
			}
			$("#selectSubject3").append(subject);
		});
		initMajorSelect("#selectMajor1");
		initMajorSelect("#selectMajor2");
		initMajorSelect("#selectMajor3");
		
		initGradeSelect("#selectGrade1");
		initGradeSelect("#selectGrade3");
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
	
	$("#logout").click(function() {//登出操作
		var r=confirm("确定要登出吗(｀・ω・´)?");
		if (r==true) {
			location = "./logout.action?id=" + id;
		}
	});
	
	$("#navStudentManage").click(function() {//学生管理块
		$("#studentManage").removeClass("displayNone");
		$("#scoreManage").addClass("displayNone");
		$("#subjectManage").addClass("displayNone");
	});
	
	$("#navSubjectManage").click(function() {//科目管理块
		$("#subjectManage").removeClass("displayNone");
		$("#scoreManage").addClass("displayNone");
		$("#studentManage").addClass("displayNone");
	});
	
	$("#navScoreManage").click(function() {//成绩管理块
		$("#scoreManage").removeClass("displayNone");
		$("#subjectManage").addClass("displayNone");
		$("#studentManage").addClass("displayNone");
	});
	
	$(".btn.btn-success.top30.floatRight").click(function() {//查看或录入
		var queryJSON = "";
		var success = function(json) {
			
		};
		var queryId = this.id;
		var data = "";
		var success = function(json) {console.log("原始", json);};
		if(queryId == "btnQueryStudents") {
			data = "@text=queryStudents&grade=" + $("#selectGrade1").val() + "&major=" + $("#selectMajor1").val() + "@";
			
			success = function(json) {
				$("#tbodyStudent tr").remove();
				var tableStr = "";
				for(var i = 0; i < json.length; i++) {
					var temp = json[i];
					tableStr += "<tr><td>" + temp["id"] + "</td><td>" + temp["name"] + "</td><td>"
						+ temp["IDNumber"] + "</td><td>" + temp["status"] + "</td></tr>"
				}
				$("#tbodyStudent").append(tableStr);
			};
		} else if(queryId == "btnInputStudents") {
			var r=confirm("请输入录入学生数");
			if (r==true) {
				inputStudent();
			}
			data = "inputStudents";
		} else if(queryId == "btnQuerySubject") {
			data = "@text=querySubject&major=" + $("#selectMajor2").val() + "@";
			success = function(json) {
				console.log(json)
				$("#tbodySubject tr").remove();
				var tableStr = "";
				for(var i in json) {
					tableStr += "<tr><td>" + i + "</td><td>" + json[i] + "</td></tr>";
				}
				$("#tbodySubject").append(tableStr);
			};
		} else if(queryId == "btnInputSubject") {
			
		} else if(queryId == "btnQueryScore") {
			data = "@text=queryScore&grade=" + $("#selectGrade3").val() + "&major=" + $("#selectMajor3").val() + "&subject=" + $("#selectSubject3").val() + "@";
			success = function(json) {
				$("#tbodyScore tr").remove();
				var tableStr = "";
				for(var i in json) {
					tableStr += "<tr><td>" + i + "</td><td>" + json[i]["name"] + "</td><td>"
						+ json[i]["score"] + "</td><td>" + json[i]["makeupScore"] + "</td></tr>";
				}
				$("#tbodyScore").append(tableStr);
			};
		} else if(queryId == "btnInputScore") {
			
		}
		console.log(data);
		$.ajax({
			url : "./teacherManage.action",
			type : "POST",
			data : {
				id : id,
				data: data
			},
			async : true,
			dataType : "json",
			success : success,
			error : function(json) {console.log("error", json);}
		});
	});
	
	var inputStudent = function() {
		
	};
});