<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
$connect = mysqli_connect("localhost", "root", "", "cantin");
mysqli_query($connect, "SET NAMES 'utf8'");
?>
