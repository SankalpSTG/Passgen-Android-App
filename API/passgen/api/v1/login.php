<?php
	require "connections/database/dbh.inc.php";
	require "validations/validations.php";
	
	$message = array();
	
	if(isset($_POST["unique_id"]) && isset($_POST["device_id"]) && isset($_POST["auth_key"]) && isset($_POST["master_password"])){
		$unique_id = mysqli_real_escape_string($conn, $_POST["unique_id"]);
		$device_id = mysqli_real_escape_string($conn, $_POST["device_id"]);
		$auth_key = mysqli_real_escape_string($conn, $_POST["auth_key"]);
		$master_password = mysqli_real_escape_string($conn, $_POST["master_password"]);
		
		//Validating Important Fields
		$message = validate_1($unique_id, $device_id, $master_password);
		
		if(!$message["error"]){
		$stmt = $conn->prepare("UPDATE pg_users SET auth_key = ? WHERE unique_id = ?");
			$stmt->bind_param("ss", $auth_key, $unique_id);
			if($stmt->execute()){
				$stmt = $conn->prepare("INSERT INTO signature_logs (device_id) VALUES(?)");
				$stmt->bind_param("s", $device_id);
				if($stmt->execute()){
					$message = array("error" => false, "error_code" => 100, "message" => "Login Successful");
					echo json_encode($message);
				}else{
					$message = array("error" => false, "error_code" => 109, "message" => "Failed To Create Logs");
					echo json_encode($message);
				}
			}else{					
				$message = array("error" => false, "error_code" => 110, "message" => "Failed To Log In");
				echo json_encode($message);
			}
		}else{
			echo json_encode($message);
		}
	}else{
		$message = array("error" => true, "error_code" => 105, "error_message" => "No Data Received");
		echo json_encode($message);
	}
?>