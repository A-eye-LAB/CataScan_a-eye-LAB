import React, { useEffect, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Card, CardContent } from '@/components/ui/card';
import { cn } from '@/lib/utils/tailwind';

type TAutocompleteInput = React.ComponentProps<'input'> & {
    suggestions: string[];
    value: string;
    onChange: (value: string) => void;
};

function AutocompleteInput(props: TAutocompleteInput) {
    const { suggestions, className, onChange, value, ...others } = props;

    const [query, setQuery] = useState<string>(value || ''); // 초기값을 value로 설정
    const [filteredSuggestions, setFilteredSuggestions] = useState<string[]>(
        []
    );
    const [showDropdown, setShowDropdown] = useState<boolean>(false);
    const [activeIndex, setActiveIndex] = useState<number>(-1);

    const handleInputChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ): void => {
        const newQuery = event.target.value;
        setQuery(newQuery);

        if (newQuery) {
            const filtered = suggestions.filter((item) =>
                item.toLowerCase().includes(newQuery.toLowerCase())
            );
            setFilteredSuggestions(filtered);
            setShowDropdown(filtered.length > 0);
            setActiveIndex(-1);
        } else {
            setShowDropdown(false);
        }

        if (onChange) {
            onChange(newQuery); // react-hook-form의 onChange 호출
        }
    };

    const handleSelect = (item: string): void => {
        setQuery(item);
        setShowDropdown(false);
        setActiveIndex(-1);
        if (onChange) {
            onChange(item); // 선택한 아이템을 react-hook-form에 전달
        }
    };

    const handleKeyDown = (
        event: React.KeyboardEvent<HTMLInputElement>
    ): void => {
        if (event.key === 'ArrowDown') {
            setActiveIndex((prevIndex) =>
                prevIndex < filteredSuggestions.length - 1 ? prevIndex + 1 : 0
            );
        } else if (event.key === 'ArrowUp') {
            setActiveIndex((prevIndex) =>
                prevIndex > 0 ? prevIndex - 1 : filteredSuggestions.length - 1
            );
        } else if (event.key === 'Enter') {
            if (activeIndex >= 0) {
                handleSelect(filteredSuggestions[activeIndex]);
            } else {
                setShowDropdown(false);
            }
        } else if (event.key === 'Escape') {
            setShowDropdown(false);
        }
    };
    const handleFocus = () => {
        setFilteredSuggestions(suggestions);
        setShowDropdown(true);
    };
    const handleBlur = (): void => {
        const standard = 200;

        setTimeout(() => {
            setShowDropdown(false);
        }, standard);
    };
    const highlightMatch = (text: string, query: string): React.ReactNode => {
        if (!query) {
            return text;
        }

        const regex = new RegExp(`(${query})`, 'gi');
        const parts = text.split(regex);

        return parts.map((part, index) =>
            regex.test(part) ? (
                <span key={index} className="text-blue-500 font-semibold">
                    {part}
                </span>
            ) : (
                part
            )
        );
    };

    useEffect(() => {
        if (value) {
            setQuery(value);
        }
    }, [value]);

    return (
        <div className="relative w-full ">
            <Input
                value={query}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                className={cn('w-full', className)}
                onFocus={handleFocus}
                onBlur={handleBlur}
                {...others}
            />
            {showDropdown && (
                <Card className="absolute top-full left-0 w-full mt-2 rounded-lg shadow-lg z-10">
                    <CardContent className={'p-2 pt-0'}>
                        <ul>
                            {filteredSuggestions.map((item, index) => (
                                <li
                                    key={index}
                                    className={cn(
                                        'p-2 hover:bg-CATASCAN-input-autocomplete-hover',
                                        activeIndex === index &&
                                            'bg-CATASCAN-input-autocomplete-hover',
                                        index === 0 && 'rounded-t-lg',
                                        index ===
                                            filteredSuggestions.length - 1 &&
                                            'rounded-b-lg'
                                    )}
                                    onMouseDown={() => handleSelect(item)} // MouseDown으로 선택 처리
                                >
                                    {highlightMatch(item, query)}
                                </li>
                            ))}
                        </ul>
                    </CardContent>
                </Card>
            )}
        </div>
    );
}

export default AutocompleteInput;
