<?php
require "DBConnect.php";

// Kiểm tra xem phương thức là POST hay không
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Kiểm tra xem transaction_id có được gửi lên hay không
    if (isset($_POST['acc_id']) && isset($_POST['order_status'])) {
        $acc_id = $_POST['acc_id'];
        $order_status = $_POST['order_status'];
        
        // Chuẩn bị câu truy vấn SQL sử dụng Prepared Statement
        $sql = "SELECT * FROM orders WHERE acc_id = ? && order_status = ?";
        $stmt = $connect->prepare($sql);
        $stmt->bind_param("ss", $acc_id, $order_status);
        $stmt->execute();

        // Lấy kết quả
        $result = $stmt->get_result();

        // Kiểm tra kết quả trả về
        if ($result->num_rows > 0) {
            // Mảng để lưu trữ dữ liệu
            $data = array();

            // Lặp qua từng hàng dữ liệu
            while ($row = $result->fetch_assoc()) {
                // Thêm dữ liệu vào mảng
                $data[] = $row;
            }

            // Trả về dữ liệu dưới dạng JSON
            echo json_encode(array('data' => $data));
        } else {
            // Trường hợp không tìm thấy dữ liệu
            echo json_encode(array('message' => 'Không có dữ liệu'));
        }
    } else {
        // Trường hợp thiếu thông tin
        echo json_encode(array('message' => 'Thiếu thông tin'));
    }
} else {
    // Phương thức không hợp lệ
    echo json_encode(array('message' => 'Phương thức không hợp lệ'));
}

// Đóng kết nối đến cơ sở dữ liệu
mysqli_close($connect);
?>
