define i32 @main() {
  %1 = alloca i1
  %2 = alloca i8
  %3 = alloca i16
  %4 = alloca i32
  %5 = alloca i64
  %6 = alloca half
  %7 = alloca float
  %8 = alloca double
  %9 = alloca fp128
  %10 = alloca x86_fp80
  %11 = alloca ppc_fp128
  %12 = alloca x86_mmx
  store i1 true, i1* %1
  store i1 false, i1* %1
  store i8 0, i8* %2
  store i8 1, i8* %2
  store i16 0, i16* %3
  store i16 1, i16* %3
  store i32 0, i32* %4
  store i32 1, i32* %4
  store i64 0, i64* %5
  store i64 1, i64* %5
  store float 0.000000, float* %7
  store float 1.000000, float* %7
  store double 0.000000, double* %8
  store double 1.000000, double* %8
  ret i32 0
}
