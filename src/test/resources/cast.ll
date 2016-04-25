define i32 @main() {
  %1 = trunc i32 -1 to i1
  %2 = sext i1 true to i32
  %3 = zext i1 true to i32
  %4 = fptrunc double 1.000000 to float
  %5 = fpext float 1.000000 to double
  %6 = fptoui float 1.000000 to i32
  %7 = fptosi float 1.000000 to i32
  %8 = uitofp i32 1 to float
  %9 = sitofp i32 1 to float
  %10 = alloca i32
  %11 = ptrtoint i32* %10 to i64
  %12 = inttoptr i64 %11 to i32*
  %13 = bitcast float 0.000000 to i32
  ret i32 0
}
