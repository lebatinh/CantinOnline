<?php
require "DBConnect.php";

if (isset($_POST['acc_id']) && isset($_POST['avatar']) && isset($_POST['phonenumber']) && isset($_POST['name']) && isset($_POST['birthday'])  && isset($_POST['address']) && isset($_POST['hometown']) && isset($_POST['stdcard_img']) && isset($_POST['type']) && isset($_POST['history'])) {
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $avatar = mysqli_real_escape_string($connect, $_POST['avatar']);
    $phonenumber = mysqli_real_escape_string($connect, $_POST['phonenumber']);
    $name = mysqli_real_escape_string($connect, $_POST['name']);
    $birthday = mysqli_real_escape_string($connect, $_POST['birthday']);
    $address = mysqli_real_escape_string($connect, $_POST['address']);
    $hometown = mysqli_real_escape_string($connect, $_POST['hometown']);
    $stdcard_img = mysqli_real_escape_string($connect, $_POST['stdcard_img']);
    $type = mysqli_real_escape_string($connect, $_POST['type']);
    $history = mysqli_real_escape_string($connect, $_POST['history']);

    // Kiểm tra sự tồn tại trong bảng account
    $checkQuery = "SELECT COUNT(*) as count FROM account WHERE phonenumber = ? AND acc_id = ?";
    $stmt = mysqli_prepare($connect, $checkQuery);

    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "ss", $phonenumber, $acc_id);
        mysqli_stmt_execute($stmt);
        mysqli_stmt_bind_result($stmt, $count);
        mysqli_stmt_fetch($stmt);
        mysqli_stmt_close($stmt);

        if ($count > 0) {
            // acc_id đã tồn tại trong bảng account, tiến hành thêm vào bảng information_user
            $insertQuery = "INSERT INTO information_user (acc_id, avatar, phonenumber, name, birthday, address, hometown, stdcard_img) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            $stmt = mysqli_prepare($connect, $insertQuery);

            if ($stmt) {
                mysqli_stmt_bind_param($stmt, "ssssssss", $acc_id, $avatar, $phonenumber, $name, $birthday, $address, $hometown, $stdcard_img);
                if (mysqli_stmt_execute($stmt)) {
                    // Thành công, cập nhật trạng thái xác thực của tài khoản
                    $isverify = "verified";
                    $verifyQuery = "UPDATE account SET isverify = ? WHERE acc_id = ?";
                    $stmt_verify = mysqli_prepare($connect, $verifyQuery);

                    if ($stmt_verify) {
                        mysqli_stmt_bind_param($stmt_verify, "ss", $isverify, $acc_id);
                        if (mysqli_stmt_execute($stmt_verify)) {
                            // Thêm dữ liệu vào bảng account_history
                            $history_query = "INSERT INTO account_history (acc_id, type, history) VALUES (?, ?, ?)";
                            $history_stmt = mysqli_prepare($connect, $history_query);

                            if ($history_stmt) {
                                mysqli_stmt_bind_param($history_stmt, "sss", $acc_id, $type, $history);
                                if (mysqli_stmt_execute($history_stmt)) {
                                    // Thêm acc_id vào bảng finance với số dư ban đầu là 0
                                    $finance_query = "INSERT INTO finance (acc_id, sodu) VALUES (?, 0)";
                                    $finance_stmt = mysqli_prepare($connect, $finance_query);
                                    if ($finance_stmt) {
                                        mysqli_stmt_bind_param($finance_stmt, "s", $acc_id);
                                        if (mysqli_stmt_execute($finance_stmt)) {
                                            echo "Xác thực thông tin thành công!";
                                        } else {
                                            echo "Lỗi thêm tài khoản thanh toán!";
                                        }
                                        mysqli_stmt_close($finance_stmt);
                                    } else {
                                        echo "Lỗi đăng ký số dư tài khoản!";
                                    }
                                } else {
                                    echo "Lỗi ghi lịch sử!";
                                }
                                mysqli_stmt_close($history_stmt);
                            } else {
                                echo "Lỗi chuẩn bị ghi lịch sử!";
                            }
                        } else {
                            echo "Xác thực thông tin thất bại!";
                        }
                        mysqli_stmt_close($stmt_verify);
                    } else {
                        echo "Lỗi xác thực thông tin!";
                    }
                } else {
                    echo "Lưu dữ liệu thất bại!";
                }
            } else {
                echo "Lỗi chuẩn bị dữ liệu!";
            }
            mysqli_stmt_close($stmt);
        } else {
            echo "Có vẻ như tài khoản của bạn không tồn tại!";
        }
    } else {
        echo "Lỗi kiểm tra tài khoản!";
    }
} else {
    echo "Lỗi đường truyền!";
}

mysqli_close($connect);
?>
