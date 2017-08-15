(function() {
		userBaseInfo = function() {
		var id;
		var password;
		
		this.getUserBaseInfo = function() {
			return {
				id : id,
				password : password
			};
		};
		
		this.getId = function() {
			return id;
		};
		
		this.getPassword = function() {
			return password;
		};
		
		this.setId = function(_id) {
			id = _id;
		};
		
		this.setPassword = function(_password) {
			password = _password;
		};
	}
}());
		