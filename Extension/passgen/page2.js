document.getElementById("qrcodedone").addEventListener('click', authenticateuser);
function authenticateuser(){
	var deviceid = localStorage.getItem("passgen_device_id");
	if(deviceid === null){
		return JSON.parse({"error":true, "error_code":101, "message":"device id is null"});
	}
	if(window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}else{
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if (this.readyState==4 && this.status==200) {
			var resp = JSON.parse(this.response);
			if(parseInt(resp.error_code) === 105){
				setQR();
				hidethis("c-page2");
				showthis("c-page6");
				invertedTheme();
			}else if(parseInt(resp.error_code) === 100){
				getUserNames();
				hidethis("c-page2");
				showthis("c-page5");
				normalTheme();
			}
		}
	}
	xmlhttp.open("GET","http://localhost/passgen/api/v3/get_access.php?device_id="+deviceid,true);
	xmlhttp.send(deviceid);
}
function hidethis(id){
	document.getElementById(id).style.display = "none";
}
function showthis(id){
	document.getElementById(id).style.display = "block";
}