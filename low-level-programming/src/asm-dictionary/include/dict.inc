%ifndef DICT_EXPORT
  %ifdef BUILDING_DICT
    %define DICT_EXPORT global
  %else
    %define DICT_EXPORT extern
  %endif
%endif

DICT_EXPORT find_word
