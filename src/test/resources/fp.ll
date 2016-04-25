define i32 @main() {
  %1 = alloca float, align 4
  %2 = alloca float, align 4  
  store float 0.000000, float* %1, align 4
  store float 0.000000, float* %2, align 4
  %3 = load float, float* %1, align 4
  %4 = load float, float* %2, align 4
  %5 = fadd float %3, %4
  %6 = fadd nnan float %3, %4
  %7 = fadd ninf float %3, %4
  %8 = fadd nsz float %3, %4
  %9 = fadd arcp float %3, %4
  %10 = fadd nnan ninf float %3, %4
  %11 = fadd nnan nsz float %3, %4
  %12 = fadd nnan arcp float %3, %4
  %13 = fadd nnan ninf nsz float %3, %4
  %14 = fadd nnan ninf arcp float %3, %4
  %15 = fadd nnan ninf nsz arcp float %3, %4
  %16 = fadd fast float %3, %4
  %17 = fsub float %3, %4
  %18 = fsub nnan float %3, %4
  %19 = fmul float %3, %4
  %20 = fmul nnan float %3, %4
  %21 = fdiv float %3, %4
  %22 = fdiv nnan float %3, %4
  %23 = frem float %3, %4
  %24 = frem nnan float %3, %4
  %25 = fcmp oeq float %3, %4
  %26 = fcmp ogt float %3, %4
  %27 = fcmp oge float %3, %4
  %28 = fcmp olt float %3, %4
  %29 = fcmp ole float %3, %4
  %30 = fcmp one float %3, %4
  %31 = fcmp ord float %3, %4
  %32 = fcmp ueq float %3, %4
  %33 = fcmp ugt float %3, %4
  %34 = fcmp uge float %3, %4
  %35 = fcmp ult float %3, %4
  %36 = fcmp ule float %3, %4
  %37 = fcmp une float %3, %4  
  %38 = fcmp uno float %3, %4   
  %39 = fcmp true float %3, %4 
  %40 = fcmp false float %3, %4
  ret i32 0
}
