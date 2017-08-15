window.Class = function(parent) {
	var klass = function() {
		this.init.apply(this, arguments);
	};
	
	if(parent && (typeof parent) === 'object' && !(parent instanceof Array)) {
		var subclass = {};
		subclass.prototype = parent.prototype;
		klass.prototype = new subclass;
	}
	
	klass.prototype.init = function() {};
	
	klass.fn = klass.prototype;
	
	klass.fn.parent = klass;
	
	klass.extend = function(obj) {
		var extended = obj.extended;
		for(var i in obj) {
			klass[i] = obj[i];
		}
		
		if(extended) extended(klass);
	};
	
	klass.include = function(obj) {
		var included = obj.included;
		for(var i in obj) {
			klass.fn[i] = obj[i];
		}
		
		if(included) included(klass);
	};
	
	klass.proxy = function(func) {
		var me = this;
		return (function() {
			return func.applay(me, arguments);
		});
	};
	
	klass.fn.proxy = klass.proxy;
	
	return klass;
};

window.mecApp = new Class;

(function (){
	if(typeof $$ === 'undefined') {
		mecProject = {
			html : {},
			error : {},
			includeMec : function(projectName) {
				if(typeof projectName === 'undefined' || typeof projectName !== 'string') {
					return;
				}
				
				if($$[projectName]) {
					throw new Error("工程名[" + projectName + "]已存在！");
				}
				
				$$[projectName] = {};
			},
			mkObj : function(projectName, objectName) {
				if(arguments.length < 1 || arguments.length > 2 
						|| arguments.length == 1 && projectName.indexOf(".") == -1) {
					var errMsg = "mkObj()用法:\n";
					errMsg += "mkOjb(<工程名称>, <项目名称>); 或\n";
					errMsg += "mkOjb(<工程名称.项目名称>);";
					
					throw new Error(errMsg);
				}
				if(objectName === undefined) {
					var names = projectName.split(".");
					projectName = names[0];
					objectName = names[1];
				}
				
				if(!$$[projectName]) {
					this.includeMec(projectName);
				}
	
				var project = $$[projectName];
				if(project[objectName]) {
					return;
				}
				
				project[objectName] = {
					controll : {},
					service : {},
					view : {},
					model : {},
				};
			}
		};
		$$ = mecProject;
	}
}());

