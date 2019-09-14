function getPassGenAuthKey(deviceid){
	return 0;
}
/*function getPassGenAuthKey(deviceid){
	if(window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}else{  
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if (this.readyState==4 && this.status==200) {
			//alert("Responded");
			alert("Hello World");
		}
	}
	xmlhttp.open("POST","https://www.techfriendsindia.com/notofthissite/passgen/ext_login.php",true);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xmlhttp.send("deviceid="+deviceid);
}*/