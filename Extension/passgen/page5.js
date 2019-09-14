document.getElementById("verifymp").addEventListener('click', verifymp);


function verifymp(){
	var deviceid = localStorage.getItem("passgen_device_id");
	var masterpassword = document.getElementById("masterpassword").value;
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
			if(resp.error_code === 100){
				hidethis("c-page1");
				hidethis("c-page2");
				hidethis("c-page4");
				hidethis("c-page5");
				showthis("c-page3");
			}else{
				document.getElementById("respotext").innerHTML = resp.message;
			}
		}
	}
	xmlhttp.open("GET","http://localhost/passgen/api/v3/ext_verify_mp.php?device_id="+deviceid + "&master_password="+masterpassword,true);
	xmlhttp.send(deviceid);
}

function hidethis(id){
	document.getElementById(id).style.display = "none";
}
function showthis(id){
	document.getElementById(id).style.display = "block";
}