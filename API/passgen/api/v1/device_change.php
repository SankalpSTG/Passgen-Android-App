<?php
	require "connections/database/dbh.inc.php";
	require "validations/validations.php";
	
	$message = array();
	
	if(isset($_POST["unique_id"]) && isset($_POST["master_password"]) && isset($_POST["device_id"]) && isset($_POST["secret_answer"])){
		$unique_id = mysqli_real_escape_string($conn, $_POST["unique_id"]);
		$master_password = mysqli_real_escape_string($conn, $_POST["master_password"]);
		
		//Validating Important Fields
		$message = validate_2($unique_id, $master_password);
		
		if(!$message["error"]){
			$stmt = $conn->prepare("SELECT secret_answer, master_password FROM pg_users WHERE unique_id = ?");
			$stmt->bind_param("s", $unique_id);
			if($stmt->execute()){
				$result = $stmt->get_result();
				
				echo json_encode($message);
				exit();
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