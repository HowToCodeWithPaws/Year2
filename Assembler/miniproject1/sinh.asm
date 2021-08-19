format PE console
entry start
include 'win32a.inc'

section '.code' code readable executable
  sinh:
    finit
    fld1
    fstp Qword[delta];����� �� 64 ���

    ; ������� � ������ ���������� ���������������� ������
    mov ecx, 0
    sinh_loop:
    inc ecx
    ; ��������� delta �� x / i
    fld Qword[x]
    mov [i], ecx
    fidiv dword[i]
    fmul Qword[delta]
    fstp Qword[delta]

    ; ���������� ������ ������� i, ������ ��� ����� ��������������
    ; � ����� �������� ��������
    mov eax, ecx
    and eax, 1
    cmp eax, 0
    je sinh_loop

    ; ���������� delta � res
    fld Qword[delta]
    fadd Qword[res]
    fstp Qword[res]

    ; ���������� ��, ����� ������� ������� ����� ���������� ���������� �� ���������� 0%.
    ; � ������� 0.001, � �� 0, �� ����� �������� ����� �� ����� ���� ������, ��� �� �����
    ; abs(delta / res) <= 0 (��� 0.001 �� ����� ����� ��������� ��������)
    fldz ;������ 64-������ ����� ��� fraction
    fld Qword[delta]
    fdiv Qword[res]
    fabs
    fcompp
    fstsw ax
    sahf
    jbe sinh_end

    ; �� ������ ������ ������ ����� �� ���������� ��������,
    ; ����� ����� �������� ����������� ������
    cmp ecx, 69420
    jl sinh_loop
    sinh_end:
    ret

  start:
    ; ������� float64 � scanf, ��������� �������� ��� ������
    push beginStr
    call [printf]
    add esp, 4
    push x
    push scanfFormat
    call [scanf]
    add esp, 8
    cmp eax, 1
    jne start_wrongInput ; ��������� �� ������������ �����. ���� ������������ - �������� � �����������

    start_scanfEnd:
    call sinh ; �������� ������

    ; printf �������� ������ � float64, � push - ���, ������� ���������
    push dword[res+4]
    push dword[res]
    push dword[x+4]
    push dword[x]
    push answerStr
    call [printf]
    add esp, 20

    ; ��������� ���������
    start_exit:
    push exitStr
    call [printf]
    call [getch]
    call [exit]

    ; ��������� ��������� ��� ������������ �����
    start_wrongInput:
    push errorStr
    call [printf]
    add esp, 4
    jmp start_exit

section '.data' data readable writable
  scanfFormat: db '%lf',0
  answerStr: db 'sinh(%g) = %.15g',10,0 ; ������� 15 ������, ������ ��� � ������ �������� 15 ���������� ������
  beginStr: db 'Which number will be our next victim for sinh function? ',0
  errorStr: db 'Wrong input - not a float',10,0
  exitStr: db 'My work here is done',10,0
  i: dd 0
  x: dq 0
  res: dq 0
  delta: dq 0

section '.idata' import code readable
  library msvcrt, 'msvcrt.dll'
  import msvcrt, printf, 'printf', scanf, 'scanf', exit, '_exit', getch, '_getch'
