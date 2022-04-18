%define head 0

%macro colon 2
%%next: 
  dq head
  db %1, 0
%2:
%define head %%next
%endmacro
