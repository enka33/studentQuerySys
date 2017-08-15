(function(){
	$$.util = {
		location : function(url, parameter) {
			if(parameter && typeof parameter === 'object' && !(parameter instanceof Array)) {
				var para = []; 
				var i = 0;
				for(var key in parameter) {
					para[i++] = key + "=" + parameter[key]; 
				}
				url += "?" + para.join("&");
			}
			return url;
		},
		urlToObj : function(location) {
			if(typeof location === 'undefined' || typeof location.search === 'undefined') {
				return null;
			}
			var search = decodeURIComponent(location.search);
			var infoList = search.substring(1).split("&");
			var res = {};
			for(var i = 0; i < infoList.length; i++) {
				var info = infoList[i].split("=");
				res[info[0]] = info[1];
			}
			
			return res;
		},
		hashCode:function(string) {
			var isNull = function(string) {
				return string == null || string.value == "";
			};
			
			var switchHashCodeToInt = function(num) {
				var MAX_VALUE = 0x7fffffff;  
				var MIN_VALUE = -0x80000000;  
				if(num > MAX_VALUE || num < MIN_VALUE) {  
				    return num &= 0xFFFFFFFF;  
				}  
				return num;  
			};

			var hashCode = 0;
			
			if(!isNull(string)) {
				for(var index = 0; index < string.length; index++) {
					hashCode = hashCode * 31 + string.charCodeAt(index);  
					hashCode = switchHashCodeToInt(hashCode);
				}
			}
			
			return hashCode;
		},
		_checkArg : function(arg, express, errMsg) {
			if(typeof arg !== 'string' || typeof arg === 'string' && arg.indexOf(".") == -1
					|| express) {
				throw new Error(errMsg);
			}
			var names = arg.split(".");
			if(!$$[names[0]]) {
				throw new Error("工程:[" + names[0] + "]不存在!");
			}
			if(!$$[names[0]][names[1]]) {
				throw new Error("工程:[" + names[0] + "]下的项目[" + names[1] + "]不存在!");
			}
			
			return names;
		},
		setHtml:function(htmlObj) {
			if(typeof htmlObj !== 'object' || htmlObj instanceof Array) {
				alert("html必须是对象！");
				return;
			}
			for(var htmlName in htmlObj) {
				if($$.html[htmlName]) {
					console.log("html:[" + htmlName + "]已存在!\n" + $$.html[htmlName]);
				} else {
					$$.html[htmlName] = htmlObj[htmlName];
				}
			}
			return $$;
		},
		getHtml : function(htmlName) {
			var html = $$.html[htmlName];
			if(typeof html === 'undefined') {
				throw new Error("html模板[" + htmlName + "]不存在！");
			}
			
			return html;
		},
		getIdSet : function(objectName, doc) {
			var names = $$.util._checkArg(objectName, false,
					"getIdSet()用法:\ngetIdSet(<工程名称.项目名称>[, document])");
			var projectName = names[0];
			var objectName = names[1];
			
			doc = doc || document;
			
			var allEle = doc.all;
			var view = {};
			for(var ele in allEle) {
				if(allEle[ele].id) {
					view[allEle[ele].id] = 
						$$[projectName][objectName].view[allEle[ele].id]
						|| {
						element : allEle[ele]	
					};
				}
			}
			$$[projectName][objectName].view = view;
		},
		getId : function(objectName, idName) {
			var names = $$.util._checkArg(objectName, typeof idName !== 'string',
					"getId()用法:\ngetId(<工程名称.项目名称>, 元素id)");
			var projectName = names[0];
			var objectName = names[1];
			
			var ele = $$[projectName][objectName].view[idName];
			if(ele) {
				return ele.element;
			}
			throw new Error(projectName + "." + objectName + "下的元素[" + idName + "]不存在！");
		},
		getMatchIndex : function(str) {
			var matchNum = 0;
			var first = true;
			var leftBracketIndex = 0;
			var rightBracketIndex = 0;
			
			while(first == true || matchNum > 0) {
				first = false;
				if(leftBracketIndex != -1) {
					leftBracketIndex = str.indexOf("!!{", leftBracketIndex);
				}
				rightBracketIndex = str.indexOf("}!!", rightBracketIndex);

				if(rightBracketIndex == -1) {
					throw new Error("[" + str + "]右括号缺失，括号失配！");
				}
				if(leftBracketIndex == -1) {
					matchNum--;
					if(matchNum < 0) {
						throw new Error("[" + str + "]右括号多余，括号失配！");
					}
					rightBracketIndex += 3;
				} else if(leftBracketIndex < rightBracketIndex) {
					matchNum++;
					leftBracketIndex += 3;
				} else {
					matchNum--;
					rightBracketIndex += 3;
				}
			}
			
			return rightBracketIndex-3;
		},
		/* wedge 楔入 */
		wedgeHtml : function(elementId, html, parameter) {
			var errMsg = "wedgeHtml()用法:\nwedgeHtml(<工程名称.项目名称.父元素id>,"
					+ " <html模板>, <parameter对象>)";
			var names = $$.util._checkArg(elementId, 
					typeof html !== 'string' || typeof parameter !== 'object', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var parentIdName = names[2];
			if(typeof parentIdName === 'undefined') throw Error(errMsg);
			
			var projectObjectName = projectName + "." + objectName;
			var parentElement = $$.$getId(projectObjectName, parentIdName);
			if(typeof parentElement === 'undefined') 
				throw Error(projectName + "." + objectName + "下的父元素[" + parentIdName + "]不存在！");
			
			var resolveHtml = function(str, parameter) {
				var resolveParameter = function(html, parameter) {
					var addPercent = false;
					
					var strList = html.split("%");
					for(var i = 0; i < strList.length; i++) {
						var matchStr = strList[i].match(/^{[_$a-zA-Z]+[_$\w]*:[^\n]*}$/);
						if(matchStr !== null) {
							var str = matchStr[0];
							matchStr = str.substring(1, str.length-1);	// 取消{}
							var commaIndex = matchStr.indexOf(":");
							var key = matchStr.substring(0, commaIndex);
							strList[i] = parameter[key] 
									? parameter[key] 
									: matchStr.substring(commaIndex+1);
							addPercent = false;
						} else {
							if(addPercent) {
								strList[i] = "%" + strList[i];
							} else {
								addPercent = true;
							}
						}
					}
					
					return strList.join("");
				};
				
				var result = "";
				var index = str.indexOf("!!{");
				if(index == -1 || str.substring(index).match(/^!!{[_$a-zA-Z]+[_$\w]*:/).length <= 0) {
					return result += resolveParameter(str, parameter);
				}
				var otherString = str.substring(index);
				var matchEnd = $$.util.getMatchIndex(otherString);
				
				var leftString = str.substring(0, index);
				var middleString = otherString.substring(3, matchEnd);
				var rightString = otherString.substring(matchEnd+3);
					
				result += resolveParameter(leftString, parameter);

				var commaIndex = middleString.indexOf(":");
				var listName = middleString.substring(0, commaIndex);

				var arrayParameter = parameter[listName];
				if(!(arrayParameter instanceof Array)) {
					throw new Error("[" + listName + "]列表参数项不正确！");
				}
				middleString = middleString.substring(commaIndex+1);
				for(var i = 0; i < arrayParameter.length; i++) {
					result += resolveHtml(new String(middleString), arrayParameter[i]);
				}
					
				result += resolveHtml(rightString, parameter);
				
				return result;
			};
			
			var htmlString = resolveHtml(html, parameter);
			$(parentElement).html(htmlString);
		},
		getHtmlParameter : function(htmlName) {
			var htmlParameter = function(str, level) {
				var simpleBlock = function(html, tabCount) {
					var res = "";
					var tab = "\t\t\t\t\t\t\t\t\t\t";
					var objTmp = {};
					
					res += "\n";
					var strList = html.split("%");
					for(var i = 0; i < strList.length; i++) {
						var arrMatch = strList[i].match(/^{[_$a-zA-Z]+[_$\w]*:[^\n]*}$/);
						if(arrMatch != null) {
							var str = arrMatch[0];
							str = str.substring(1, str.length-1);
							var commaIndex = str.indexOf(":");
							var keyName = str.substring(0, commaIndex);
							var keyValue = str.substring(commaIndex+1);
							
							if(objTmp[keyName] == undefined) {
								res += tab.substring(0, tabCount) + keyName + " : ";
								res += (keyValue.length == 0) ? "\"\", /*必填项！*/\n" 
										: "\"" + keyValue + "\",\n";
								objTmp[keyName] = keyValue;
							}
						}
					}
					
					return res;
				};
				
				var result = "";
				var tab = "\t\t\t\t\t\t\t\t\t\t";
				var index = str.indexOf("!!{");
				if(index != -1 && str.substring(index).match(/^!!{[_$a-zA-Z]+[_$\w]*:/).length > 0) {
					var index = str.indexOf("!!{");
					if(index == -1 || str.substring(index).match(/^!!{[_$a-zA-Z]+[_$\w]*:/).length <= 0) {
						return result += resolveParameter(str, parameter);
					}
					var otherString = str.substring(index);
					var matchEnd = $$.util.getMatchIndex(otherString);
					
					var leftString = str.substring(0, index);
					var middleString = otherString.substring(3, matchEnd);
					var rightString = otherString.substring(matchEnd+3);
					
					result += simpleBlock(leftString, level);
		
					var commaIndex = middleString.indexOf(":");
					var listName = middleString.substring(0, commaIndex);
					result += tab.substring(0, level) + listName + " : [{";
					middleString = middleString.substring(commaIndex+1);
					result += htmlParameter(middleString, level+1);
					result += tab.substring(0, level) + "},],";
					
					result += htmlParameter(rightString, level);
				} else if(str.length > 0) {
					result += simpleBlock(str, level);
				}
				
				return result;
			};
			
			if(typeof htmlName !== 'string') {
				return "参数[" + htmlName + "]类型:" + (typeof htmlName) + "不正确!";
			}
			var html = $$.html[htmlName];
			if(typeof html !== 'string') {
				return "html模板:[" + htmlName + "]不存在!";
			}
			var str = "{";
			str += htmlParameter(html, 1);
			str += "}\n\n";
			
			return str;
		},
		setModel : function(objectName, modelName, model) {
			var errMsg = "setModel()用法:\nsetModel(<工程名称.项目名称.类名称>,"
				+ " <model名称>, <类对象>)";
			var names = $$.util._checkArg(objectName, 
					typeof modelName !== 'string', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var className = names[2];
			
			if(typeof className === 'undefined') {
				throw new Error(errMsg);
			}
			
			var Class = $$[projectName][objectName].model[className];
			if(typeof Class !== 'function') {
				throw new Error("工程[" + projectName + "]项目[" + objectName + "]类[" +
						+ className + "]不存在!");
			}
			
			var oldModel = $$[projectName][objectName].model[modelName]; 
			if(typeof oldModel !== 'undefined') {
				throw new Error(projectName + "." + objectName + "已存在模板类:" + modelName);
			}
			
			if(typeof model !== 'object') {
				$$[projectName][objectName].model[modelName] = new Class;
			} else {
				$$[projectName][objectName].model[modelName] = model;
			}
		},
		getModel : function(modelName) {
			var errMsg = "getModel()用法:\ngetModel(<工程名称.项目名称.model名称>)";
			var names = $$.util._checkArg(modelName, false, errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var modelName = names[2];
			
			var model = $$[projectName][objectName].model[modelName]; 
			if(typeof model === 'undefined') {
				throw new Error(projectName + "." + objectName + "." + modelName + "不存在!");
			}
			
			return model;
		},
		setElementAction : function(elementId, action) {
			var errMsg = "setElementAction()用法:\nsetElementAction(<工程名称.项目名称.元素id>,"
				+ " <元素基本操作对象>)";
			var names = $$.util._checkArg(elementId, typeof action !== 'object', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var elementIdName = names[2];
			if(typeof elementIdName === 'undefined') {
				throw Error(errMsg);
			}
			
			var ele = $$[projectName][objectName].view[elementIdName];
			if(ele) {
				for(var act in action) {
					ele[act] = action[act];
				}
			}
		},
		getElementAction : function(elementId, actionName) {
			var errMsg = "getElementAction()用法:\ngetElementAction(<工程名称.项目名称.元素id>,"
				+ " <元素基本操作名称>)";
			var names = $$.util._checkArg(elementId, typeof actionName !== 'string', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var elementIdName = names[2];
			if(typeof elementIdName === 'undefined') {
				throw Error(errMsg);
			}
			
			return $$[projectName][objectName].view[elementIdName][actionName];
		},
		getIdObj : function(elementId) {
			var errMsg = "getIdObj()用法:\ngetIdObj(<工程名称.项目名称.元素id>);";
			var names = $$.util._checkArg(elementId, false, errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var elementIdName = names[2];
			if(typeof elementIdName === 'undefined') {
				throw Error(errMsg);
			}
			
			return $$[projectName][objectName].view[elementIdName];
		},
		ajax : function(ajaxObj) {
			if(typeof ajaxObj === 'undefined' 
				|| typeof ajaxObj.url !== 'string') {
				return;
			}
			$.ajax({
				url: ajaxObj.url,
				method:	ajaxObj.method || "post",
		        data: ajaxObj.data || {},
		        cache: false,
		        async : ajaxObj.async || false,
		        dataType: "json",
		        success: ajaxObj.success || function(){},
		        error: ajaxObj.error || function (textStatus) { 
		        	console.log("textStatus:", textStatus);
		            alert("请求失败:[" + textStatus + "]!");
		        }
			});
		},
		setAction : function(proObj, action) {
			var errMsg = "setAction()用法:\nsetAction(<工程名称.项目名称.action名称>," +
					" action)";
			var names = $$.util._checkArg(proObj, typeof action !== 'function', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var actionName = names[2];
			
			if(typeof actionName === 'undefined') {
				throw Error(errMsg);
			}
			
			$$[projectName][objectName].controll[actionName] = action;
		},
		getAction : function(actionName) {
			var errMsg = "getAction()用法:\ngetAction(<工程名称.项目名称.action名称>)";
			var names = $$.util._checkArg(actionName, false, errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var actionName = names[2];
			
			if(typeof actionName === 'undefined') {
				throw Error(errMsg);
			}
			
			return $$[projectName][objectName].controll[actionName];
		},
		addModelClass : function(className, classes) {
			var errMsg = "addModelClass()用法:\naddModelClass(<工程名称.项目名称.模板类名称>, <类>)";
			var names = $$.util._checkArg(className, typeof classes !== 'function', errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var className = names[2];
			
			if(typeof className === 'undefined') {
				throw Error(errMsg);
			}
			
			$$[projectName][objectName].model[className] = classes;
		},
		getModelClass : function(className) {
			var errMsg = "getModelClass()用法:\ngetModelClass(<工程名称.项目名称.模板类名称>)";
			var names = $$.util._checkArg(className, false, errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var className = names[2];
			
			if(typeof className === 'undefined') {
				throw Error(errMsg);
			}
			
			return $$[projectName][objectName].model[className];
		},
		memorySizeToString : function(size) {
			if(size < 0 || size > 2147483647) {
				return "无效长度";
			}
			if(size < (1 << 10)) {
				return size + "B";
			}
			if(size < (1 << 20)) {
				size >>>= 10;
				return size + "KB";
			}
			if(size < (1 << 30)) {
				size >>>= 20;
				return size + "MB";
			}
			size >>>= 30;
			return size + "GB";
		},
		isIdElementExist : function(eleId) {
			var errMsg = "isIdElementExist()用法:\nisIdElementExist(<工程名称.项目名称.id元素名称>)";
			var names = $$.util._checkArg(eleId, false, errMsg);
			var projectName = names[0];
			var objectName = names[1];
			var idElementName = names[2];
			
			if(typeof idElementName === 'undefined') {
				throw Error(errMsg);
			}
			
			return (typeof $$[projectName][objectName].view[idElementName] === 'object');
		}
	};
	$$.$location = $$.util.location;
	$$.$urlToObj = $$.util.urlToObj;
	$$.$hashCode = $$.util.hashCode;
	$$.$setHtml = $$.util.setHtml;
	$$.$getHtml = $$.util.getHtml;
	$$.$getIdSet = $$.util.getIdSet;
	$$.$getId = $$.util.getId;
	$$.$wedgeHtml = $$.util.wedgeHtml;
	$$.$getHtmlParameter = $$.util.getHtmlParameter;
	$$.$setModel = $$.util.setModel;
	$$.$getModel = $$.util.getModel;
	$$.$setElementAction = $$.util.setElementAction;
	$$.$getElementAction = $$.util.getElementAction;
	$$.$getIdObj = $$.util.getIdObj;
	$$.$ajax = $$.util.ajax;
	$$.$setAction = $$.util.setAction;
	$$.$getAction = $$.util.getAction;
	$$.$addModelClass = $$.util.addModelClass;
	$$.$getModelClass = $$.util.getModelClass;
	$$.$memorySizeToString = $$.util.memorySizeToString;
	$$.$isIdElementExist = $$.util.isIdElementExist;
}())
