<?php
require "DBConnect.php";

if (isset($_POST['acc_id'])){
    $acc_id = $_POST['acc_id'];

    if ($acc_id == "admin") {
        // Chuẩn bị câu truy vấn SQL
        $sql = "SELECT avatar, acc_id, name FROM information_user";
        // Sử dụng Prepared Statement
        $stmt = $connect->prepare($sql);
        $stmt->execute();

        // Lấy kết quả
        $result = $stmt->get_result();
        
        // Kiểm tra kết quả trả về
        if ($result->num_rows > 0) {
        // Mảng để lưu trữ dữ liệu
            $data = array();

            // Lặp qua từng hàng dữ liệu
             while($row = $result->fetch_assoc()) {
               // Thêm dữ liệu vào mảng
                 $data[] = $row;
                }

                // Trả về dữ liệu dưới dạng JSON
                echo json_encode(array('data' => $data));
            } else {
            // Trường hợp không tìm thấy dữ liệu
            echo json_encode(array('message' => 'Không có dữ liệu'));
        }
    } else{
        // Trường hợp acc_id không hợp lệ 
    echo json_encode(array('message' => 'Bạn không có quyền này!'));
    }
} else {
    // Trường hợp acc_id không được gửi lên
    echo json_encode(array('message' => 'Thiếu thông tin mã sinh viên'));
}

mysqli_close($connect);
?>