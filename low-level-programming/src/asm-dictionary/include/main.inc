%ifndef MAIN_EXPORT
  %ifdef BUILDING_MAIN
    %define MAIN_EXPORT global
  %else
    %define MAIN_EXPORT extern
  %endif
%endif

MAIN_EXPORT _start
