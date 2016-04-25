define i32 @main() {
  %1 = alloca i8
  call void @llvm.stackrestore(i8* %1)
  %2 = alloca float, align 4
  store float 1.000000, float* %2, align 4
  %3 = load float, float* %2, align 4
  %4 = call float @square(float %3)
  %5 = call float @llvm.sqrt.f32(float %4)
  ret i32 0
}

define float @square(float %a) {
  %1 = alloca float, align 4
  store float %a, float* %1, align 4
  %2 = load float, float* %1, align 4
  %3 = fmul float %2, %2
  ret float %3
}

declare void @llvm.stackrestore(i8*)

declare float @llvm.sqrt.f32(float)
