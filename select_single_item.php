<?php
require "DBConnect.php";

// Kiểm tra xem phương thức là POST hay không
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Kiểm tra xem acc_id có được gửi lên hay không
    if (isset($_POST['item_id'])) {
        $item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
        
        // Chuẩn bị câu truy vấn SQL sử dụng Prepared Statement
        $sql = "SELECT * FROM item WHERE item_id = ?";
        $stmt = $connect->prepare($sql);

        if ($stmt) {
            // Gán giá trị cho tham số của câu lệnh SQL
            $stmt->bind_param("s", $item_id);
            
            // Thực thi câu lệnh SQL
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
            $stmt->close();
        } else {
            // Lỗi khi chuẩn bị câu lệnh SQL
            echo json_encode(array('message' => 'Lỗi thực hiện!'));
        }
    } else {
        // Trường hợp thiếu thông tin
        echo json_encode(array('message' => 'Bạn không có quyền này!'));
    }
} else {
    // Phương thức không hợp lệ
    echo json_encode(array('message' => 'Phương thức không hợp lệ'));
}

// Đóng kết nối đến cơ sở dữ liệu
mysqli_close($connect);
?>
