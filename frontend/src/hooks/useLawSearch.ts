import {useEffect, useRef, useState} from 'react';
import type { LawArticle } from '../types/law';
import { searchLawArticles } from '../api/lawApi';

export function useLawSearch() {
    const [articles, setArticles] = useState<LawArticle[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [hasSearched, setHasSearched] = useState(false);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [searchedKeyword, setSearchedKeyword] = useState('');
    const [searchedDate, setSearchedDate] = useState('');

    const searchControllerRef = useRef<AbortController | null>(null);

    const handleSearch = async (
        keyword: string,
        date: string,
        pageNumber: number = 0
    ) => {
        const trimmedKeyword = pageNumber === 0
            ? keyword.trim()
            : searchedKeyword;

        const selectedDate = pageNumber === 0
            ? date
            : searchedDate;

        if (!trimmedKeyword) {
            alert('검색어를 입력해주세요.');
            return;
        }

        // 이전 검색 요청 취소
        searchControllerRef.current?.abort();

        const controller = new AbortController();
        searchControllerRef.current = controller;

        setLoading(true);
        setError('');

        try {
            const data = await searchLawArticles(
                trimmedKeyword,
                selectedDate,
                pageNumber,
                10,
                controller.signal
            );

            if (controller.signal.aborted) return;

            setArticles(data.content);
            setPage(data.number);
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
            setSearchedKeyword(trimmedKeyword);
            setSearchedDate(selectedDate);
            setHasSearched(true);

        } catch (error) {
            if (controller.signal.aborted) return;

            console.error('검색 오류:', error);
            setError('검색 중 오류가 발생했습니다.');
            setArticles([]);
            setHasSearched(false);

        } finally {
            if (searchControllerRef.current === controller) {
                searchControllerRef.current = null;
                setLoading(false);
            }
        }
    };

    const resetSearch = () => {
        // 진행 중인 검색 요청 취소
        searchControllerRef.current?.abort();
        searchControllerRef.current = null;

        setArticles([]);
        setLoading(false);
        setError('');
        setHasSearched(false);

        setPage(0);
        setTotalPages(0);
        setTotalElements(0);

        setSearchedKeyword('');
        setSearchedDate('');
    };

    useEffect(() => {
        return () => {
            searchControllerRef.current?.abort();
        };
    }, []);

    return {
        articles,
        loading,
        error,
        hasSearched,
        page,
        totalPages,
        totalElements,
        searchedKeyword,
        searchedDate,
        handleSearch,
        resetSearch
    };
}