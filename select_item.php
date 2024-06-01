<?php
require "DBConnect.php";

if (isset($_POST['product_type'])){
    $product_type = mysqli_real_escape_string($connect, $_POST['product_type']);
    
    if ($product_type === 'Thực đơn') {
        // Truy vấn để lấy toàn bộ dữ liệu
        $sql = "SELECT * FROM item";
        $stmt = $connect->prepare($sql);
    } else {
        // Truy vấn để lấy dữ liệu với product_type cụ thể
        $sql = "SELECT * FROM item WHERE product_type = ?";
        $stmt = $connect->prepare($sql);
        $stmt->bind_param("s", $product_type);
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
    // Trường hợp product_type không được gửi lên
    echo json_encode(array('message' => 'Thiếu thông tin loại vật phẩm'));
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
