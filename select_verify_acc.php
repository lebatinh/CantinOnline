<?php
require "DBConnect.php";

// Kiểm tra phương thức là POST hay không
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Kiểm tra xem acc_id có được gửi lên hay không
    if (isset($_POST['acc_id'])) {
        $acc_id = $_POST['acc_id'];
         // Chuẩn bị câu truy vấn SQL sử dụng Prepared Statement
            $sql = "SELECT COUNT(*) as count FROM information_user WHERE acc_id = ?";
            $stmt = $connect->prepare($sql);
            $stmt->bind_param("s", $acc_id);
            $stmt->execute();

            // Lấy kết quả
            $result = $stmt->get_result();

            // Kiểm tra số dòng được trả về
            $row = $result->fetch_assoc();
            if ($row['count'] > 0) {
                echo json_encode(array('message' => 'true'));

                 // Cập nhật cột verify thành true trong bảng account
                $updateSql = "UPDATE account SET isverify = 'true' WHERE acc_id = ?";
                $updateStmt = $connect->prepare($updateSql);
                $updateStmt->bind_param("s", $acc_id);
                $updateStmt->execute();
            } else {
                echo json_encode(array('message' => 'false'));
            }
    } else {
        // Trường hợp acc_id không được gửi lên
        echo json_encode(array('message' => 'Thiếu thông tin mã sinh viên'));
    }
} else {
    // Phương thức không hợp lệ
    echo json_encode(array('message' => 'Phương thức không hợp lệ'));
}

// Đóng kết nối đến cơ sở dữ liệu
mysqli_close($connect);
?>
