<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST có tồn tại hay không
if (!empty($_POST['acc_id']) && !empty($_POST['transaction_id']) && !empty($_POST['history']) && !empty($_POST['status'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $transaction_id = mysqli_real_escape_string($connect, $_POST['transaction_id']);
    $history = mysqli_real_escape_string($connect, $_POST['history']);
    $status = mysqli_real_escape_string($connect, $_POST['status']);
    $type = mysqli_real_escape_string($connect, $_POST['type']);

    // Kiểm tra nếu status không hợp lệ
    if ($status !== "đã duyệt" && $status !== "từ chối" && $status !== "hủy đơn") {
        echo json_encode(array('message' => "Giá trị không hợp lệ!"));
        exit;
    }

    // Bắt đầu giao dịch
    mysqli_begin_transaction($connect);

    try {
        if ($status === "hủy đơn" || $status === "từ chối") {
            // Cập nhật status và history trong bảng request_change_money nếu status là "hủy đơn" hoặc "từ chối"
            $update_query = "UPDATE request_change_money SET status = ?, history = ? WHERE acc_id = ? AND transaction_id = ?";
            if ($update_stmt = mysqli_prepare($connect, $update_query)) {
                mysqli_stmt_bind_param($update_stmt, "ssss", $status, $history, $acc_id, $transaction_id);
                mysqli_stmt_execute($update_stmt);
                mysqli_stmt_close($update_stmt);

                // Commit giao dịch
                mysqli_commit($connect);

                // Phản hồi thành công
                echo json_encode(array('message' => "Hủy đơn thành công"));
            } else {
                throw new Exception("Lỗi: " . mysqli_error($connect));
            }
        } else {
            // Lấy thông tin từ bảng request_change_money với acc_id, transaction_id và status "chờ duyệt"
            $select_query = "SELECT sotien FROM request_change_money WHERE acc_id = ? AND transaction_id = ? AND status = 'chờ duyệt'";
            if ($select_stmt = mysqli_prepare($connect, $select_query)) {
                mysqli_stmt_bind_param($select_stmt, "ss", $acc_id, $transaction_id);
                mysqli_stmt_execute($select_stmt);
                mysqli_stmt_bind_result($select_stmt, $sotien);
                mysqli_stmt_fetch($select_stmt);
                mysqli_stmt_close($select_stmt);

                if ($sotien === null) {
                    throw new Exception("Không tìm thấy yêu cầu thay đổi tiền hoặc trạng thái không đúng.");
                }

                // Cập nhật status và history trong bảng request_change_money
                $update_query = "UPDATE request_change_money SET status = ?, history = ? WHERE acc_id = ? AND transaction_id = ? AND status = 'chờ duyệt' AND type = ?";
                if ($update_stmt = mysqli_prepare($connect, $update_query)) {
                    mysqli_stmt_bind_param($update_stmt, "sssss", $status, $history, $acc_id, $transaction_id, $type);
                    if (mysqli_stmt_execute($update_stmt)) {
                        // Nếu status là "đã duyệt", cập nhật sodu trong bảng finance
                        if ($status == "đã duyệt") {
                            if ($type == "nạp tiền") {
                                $update_finance_query = "UPDATE finance SET sodu = IFNULL(sodu, 0) + ? WHERE acc_id = ?";
                            } else if ($type == "rút tiền") {
                                // Kiểm tra số dư trước khi rút tiền
                                $check_balance_query = "SELECT sodu FROM finance WHERE acc_id = ?";
                                if ($check_balance_stmt = mysqli_prepare($connect, $check_balance_query)) {
                                    mysqli_stmt_bind_param($check_balance_stmt, "s", $acc_id);
                                    mysqli_stmt_execute($check_balance_stmt);
                                    mysqli_stmt_bind_result($check_balance_stmt, $sodu);
                                    mysqli_stmt_fetch($check_balance_stmt);
                                    mysqli_stmt_close($check_balance_stmt);

                                    if ($sodu >= $sotien) {
                                        $update_finance_query = "UPDATE finance SET sodu = sodu - ? WHERE acc_id = ?";
                                    } else {
                                        mysqli_rollback($connect);
                                        echo json_encode(array('message' => "Số dư không đủ"));
                                        exit;
                                    }
                                } else {
                                    mysqli_rollback($connect);
                                    echo json_encode(array('message' => "Lỗi kiểm tra số dư: " . mysqli_error($connect)));
                                    exit;
                                }
                            }

                            if (isset($update_finance_query)) {
                                if ($update_finance_stmt = mysqli_prepare($connect, $update_finance_query)) {
                                    mysqli_stmt_bind_param($update_finance_stmt, "is", $sotien, $acc_id);
                                    if (mysqli_stmt_execute($update_finance_stmt)) {
                                        // Kiểm tra xem số dòng đã bị ảnh hưởng
                                        if (mysqli_stmt_affected_rows($update_finance_stmt) > 0) {
                                            // Cập nhật thành công, commit giao dịch và phản hồi
                                            mysqli_commit($connect);
                                            echo json_encode(array('message' => "Cập nhật số dư thành công"));
                                        } else {
                                            // Không có dòng nào bị ảnh hưởng, rollback giao dịch và phản hồi lỗi
                                            mysqli_rollback($connect);
                                            echo json_encode(array('message' => "Cập nhật số dư không thành công"));
                                        }
                                    } else {
                                        // Câu lệnh SQL không thực thi thành công, rollback giao dịch và phản hồi lỗi
                                        mysqli_rollback($connect);
                                        echo json_encode(array('message' => "Lỗi: " . mysqli_error($connect)));
                                    }
                                    mysqli_stmt_close($update_finance_stmt);
                                } else {
                                    // Lỗi khi chuẩn bị câu lệnh SQL, rollback giao dịch và phản hồi lỗi
                                    mysqli_rollback($connect);
                                    echo json_encode(array('message' => "Lỗi: " . mysqli_error($connect)));
                                }
                            }
                        }
                    } else {
                        // Câu lệnh SQL không thực thi thành công, rollback giao dịch và phản hồi lỗi
                        mysqli_rollback($connect);
                        echo json_encode(array('message' => "Lỗi: " . mysqli_error($connect)));
                    }
                    mysqli_stmt_close($update_stmt);
                } else {
                    // Lỗi khi chuẩn bị câu lệnh SQL, rollback giao dịch và phản hồi lỗi
                    mysqli_rollback($connect);
                    echo json_encode(array('message' => "Lỗi: " . mysqli_error($connect)));
                }
            } else {
                throw new Exception("Lỗi: " . mysqli_error($connect));
            }
        }
    } catch (Exception $e) {
        // Rollback giao dịch nếu có lỗi
        mysqli_rollback($connect);
        echo json_encode(array('message' => "Lỗi xử lý giao dịch: " . $e->getMessage()));
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại hoặc rỗng
    echo json_encode(array('message' => "Tham số rỗng hoặc không tồn tại!"));
    exit;
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
