<?php
	function validation($unique_id, $device_id, $master_password){
		include "connections/database/dbh.inc.php";
		$message = array();
		if(empty($unique_id) || empty($device_id) || empty($master_password)){
			$message = array("error" => true, "error_code" => 101, "error_message" => "Required All Parameters");
			return $message;
		}else if(!filter_var($unique_id, FILTER_VALIDATE_EMAIL)){
			$message = array("error" => true, "error_code" => 102, "error_message" => "Email ID Not Valid");
			return $message;
		}else{
			$stmt = $conn->prepare("SELECT serial_id FROM pg_users WHERE unique_id = ?");
			$stmt->bind_param("s", $unique_id);
			if($stmt->execute();){
				$result = $stmt->get_result();
				$stmt->close();
				if(mysqli_num_rows($result) == 0){
					$message = array("error" => false, "error_code" => 100, "error_message" => "Done With Validation");
					return $message;
				}else{
					$message = array("error" => true, "error_code" => 105, "error_message" => "User Already Exists");
					return $message;
				}
			}else{
				$message = array("error" => true, "error_code" => 103, "error_message" => "Failed To Fetch Users");
				return $message;
			}
		}
	}
?>