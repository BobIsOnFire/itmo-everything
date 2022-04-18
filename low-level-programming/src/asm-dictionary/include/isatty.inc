%ifndef ISATTY_EXPORT
  %ifdef BUILDING_ISATTY
    %define ISATTY_EXPORT global
  %else
    %define ISATTY_EXPORT extern
  %endif
%endif

ISATTY_EXPORT isatty
