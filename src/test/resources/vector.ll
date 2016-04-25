define i32 @main() {
  %1 = alloca <4 x i32>, align 16
  %2 = load <4 x i32>, <4 x i32>* %1, align 16
  %3 = insertelement <4 x i32> %2, i32 0, i32 100
  %4 = extractelement <4 x i32> %3, i32 0
  %5 = shufflevector <4 x i32> %3, <4 x i32> %3, <4 x i32> <i32 0, i32 0, i32 0, i32 0>
  %6 = add <4 x i32> %3, %5
  %7 = icmp eq <4 x i32> %3, %6
  ret i32 0
}

