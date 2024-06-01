<?php
require "DBConnect.php";

// Kiểm tra xem phương thức là POST hay không
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Kiểm tra xem acc_id có được gửi lên hay không
    if (isset($_POST['acc_id'])) {
        $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);

        // Chuẩn bị câu truy vấn SQL cho acc_id với type là "chuyển tiền"
        $sql_transfer = "SELECT * FROM balance_change WHERE acc_id = ? AND type = 'chuyển tiền'";
        $stmt_transfer = $connect->prepare($sql_transfer);
        $stmt_transfer->bind_param("s", $acc_id);
        $stmt_transfer->execute();
        $result_transfer = $stmt_transfer->get_result();

        // Chuẩn bị câu truy vấn SQL cho receiver với type là "chuyển tiền"
        $sql_receive = "SELECT * FROM balance_change WHERE receiver = ? AND type = 'chuyển tiền'";
        $stmt_receive = $connect->prepare($sql_receive);
        $stmt_receive->bind_param("s", $acc_id);
        $stmt_receive->execute();
        $result_receive = $stmt_receive->get_result();

        // Chuẩn bị câu truy vấn SQL cho acc_id với type là "thanh toán hóa đơn"
        $sql_bill = "SELECT * FROM balance_change WHERE acc_id = ? AND type = 'Thanh toán hóa đơn'";
        $stmt_bill = $connect->prepare($sql_bill);
        $stmt_bill->bind_param("s", $acc_id);
        $stmt_bill->execute();
        $result_bill = $stmt_bill->get_result();

        // Mảng để lưu trữ dữ liệu
        $data = array();

        // Lấy dữ liệu từ kết quả truy vấn cho acc_id với type là "chuyển tiền"
        while ($row = $result_transfer->fetch_assoc()) {
            $data[] = $row;
        }

        // Lấy dữ liệu từ kết quả truy vấn cho receiver với type là "chuyển tiền" và thay đổi type thành "nhận tiền"
        while ($row = $result_receive->fetch_assoc()) {
            $row['type'] = 'nhận tiền';
            $data[] = $row;
        }

        // Lấy dữ liệu từ kết quả truy vấn cho acc_id với type là "thanh toán hóa đơn"
        while ($row = $result_bill->fetch_assoc()) {
            $data[] = $row;
        }

        // Kiểm tra và trả về dữ liệu dưới dạng JSON
        if (!empty($data)) {
            echo json_encode(array('data' => $data));
        } else {
            // Trường hợp không tìm thấy dữ liệu
            echo json_encode(array('message' => 'Không có dữ liệu'));
        }

        // Đóng các prepared statements
        $stmt_transfer->close();
        $stmt_receive->close();
        $stmt_bill->close();
    } else {
        // Trường hợp thiếu thông tin acc_id
        echo json_encode(array('message' => 'Thiếu thông tin mã đơn'));
    }
} else {
    // Phương thức không hợp lệ
    echo json_encode(array('message' => 'Phương thức không hợp lệ'));
}

// Đóng kết nối đến cơ sở dữ liệu
mysqli_close($connect);
?>
