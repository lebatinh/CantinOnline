<?php
require "DBConnect.php";

if (isset($_POST['receiver']) && isset($_POST['type'])) {
    $receiver = $_POST['receiver'];
    $type = $_POST['type'];

    // Thay đổi câu lệnh SQL để kiểm tra xem receiver có chứa id hoặc là 'all'
    $sql = "SELECT title, notification, timepost 
            FROM notification 
            WHERE (FIND_IN_SET(?, receiver) OR receiver = 'all') AND type = ?";

    // Sử dụng Prepared Statement
    $stmt = $connect->prepare($sql);
    $stmt->bind_param("ss", $receiver, $type);
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
        echo json_encode(array('message' => 'Không tìm thấy dữ liệu'));
    }

    // Đóng kết nối
    $stmt->close();
} else {
    // Trường hợp thiếu thông tin người nhận hoặc thể loại thông tin
    echo json_encode(array('message' => 'Thiếu thông tin người nhận hoặc thể loại thông tin'));
}

mysqli_close($connect);
?>
