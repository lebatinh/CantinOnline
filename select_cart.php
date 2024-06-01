<?php
require "DBConnect.php";

if (isset($_POST['acc_id'])) {
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    
    // Kiểm tra xem item_id có được gửi lên không
    if(isset($_POST['item_id']) && !empty($_POST['item_id'])) {
        $item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
        
        // Truy vấn để lấy dữ liệu với cả item_id và acc_id
        $sql = "SELECT cart.item_id, item.item_name, item.price, cart.quantity, item.hinhanh 
                FROM cart 
                JOIN item ON cart.item_id = item.item_id 
                WHERE cart.acc_id = ? AND cart.item_id = ?";
        $stmt = $connect->prepare($sql);
        $stmt->bind_param("ss", $acc_id, $item_id);
    } else {
        // Truy vấn để chỉ lấy dữ liệu với acc_id
        $sql = "SELECT cart.item_id, item.item_name, item.price, cart.quantity, item.hinhanh 
                FROM cart 
                JOIN item ON cart.item_id = item.item_id 
                WHERE cart.acc_id = ?";
        $stmt = $connect->prepare($sql);
        $stmt->bind_param("s", $acc_id);
    }

    // Thực thi câu lệnh SQL
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
} else {
    // Trường hợp acc_id không được gửi lên
    echo json_encode(array('message' => 'Thiếu thông tin acc_id'));
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
