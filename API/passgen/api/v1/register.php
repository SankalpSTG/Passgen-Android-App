<?php
	require "connections/database/dbh.inc.php";
	require "validations/validations.php";
	
	$message = array();
	
	if(isset($_POST["unique_id"]) && isset($_POST["device_id"]) && isset($_POST["auth_key"]) && isset($_POST["master_password"]) && isset($_POST["user_name"]) && isset($_POST["secret_question"]) && isset($_POST["secret_answer"])){
		$unique_id = mysqli_real_escape_string($conn, $_POST["unique_id"]);
		$device_id = mysqli_real_escape_string($conn, $_POST["device_id"]);
		$auth_key = mysqli_real_escape_string($conn, $_POST["auth_key"]);
		$master_password = mysqli_real_escape_string($conn, $_POST["master_password"]);
		$user_name = mysqli_real_escape_string($conn, $_POST["user_name"]);
		$secret_question = mysqli_real_escape_string($conn, $_POST["secret_question"]);
		$secret_answer = mysqli_real_escape_string($conn, $_POST["secret_answer"]);
		
		//Validating Important Fields
		$message = validate($unique_id, $device_id, $master_password);
		
		if(!$message["error"]){
			$message = validate_non_imp($user_name, $secret_question, $secret_answer, $auth_key);
			if(!$message["error"]){
				$stmt = $conn->prepare("INSERT INTO pg_users(unique_id, device_id, auth_key, master_password, user_name, secret_question, secret_answer) VALUES(?, ?, ?, ?, ?, ?, ?)");
				$encrypted_password = password_hash($master_password, PASSWORD_DEFAULT);
				$encrypted_auth = password_hash($auth_key, PASSWORD_DEFAULT);
				$stmt->bind_param("sssssss", $unique_id, $device_id, $encrypted_auth, $encrypted_password, $user_name, $secret_question, $secret_answer);
				if($stmt->execute()){
					$message = array("error" => false, "error_code" => 100, "message" => "successful");
					echo json_encode($message);
					exit();
				}else{
					$message = array("error" => true, "error_code" => 106, "message" => "Failed To Register User");
					echo json_encode($message);
					exit();
				}
			}else{
				echo json_encode($message);
			}
		}else{
			echo json_encode($message);
		}
		
	}else{
		$message = array("error" => true, "error_code" => 105, "error_message" => "No Data Received");
		echo json_encode($message);
	}
	function validate_non_imp($user_name, $secret_question, $secret_answer, $auth_key){
		$message = array();
		if(empty($user_name) || empty($secret_question) || empty($secret_answer) || empty($auth_key)){
			$message = array("error" => true, "error_code" => 101, "error_message" => "Required All Parameters");
			return $message;
		}else{
			$message = array("error" => false, "error_code" => 100, "error_message" => "Non Imp Validated");
			return $message;
		}
	}
?>