import { useEffect, useRef, useState } from 'react';
import type { LawArticleCompare } from '../types/law';
import { compareLawArticles } from '../api/lawApi';

export function useArticleCompare() {
    const [compareResult, setCompareResult] =
        useState<LawArticleCompare | null>(null);

    const [compareLoading, setCompareLoading] = useState(false);
    const [compareError, setCompareError] = useState('');

    const compareControllerRef = useRef<AbortController | null>(null);

    const handleCompare = async (
        currentMst: number,
        articleNo: number,
        branchNo: number
    ) => {
        compareControllerRef.current?.abort();

        const controller = new AbortController();
        compareControllerRef.current = controller;

        setCompareLoading(true);
        setCompareError('');
        setCompareResult(null);

        try {
            const data = await compareLawArticles(
                currentMst,
                articleNo,
                branchNo,
                controller.signal
            );

            if (controller.signal.aborted) return;

            setCompareResult(data);

        } catch (error) {
            if (controller.signal.aborted) return;

            setCompareError(
                error instanceof Error
                    ? error.message
                    : '법령 비교 중 오류가 발생했습니다.'
            );

        } finally {
            if (compareControllerRef.current === controller) {
                compareControllerRef.current = null;
                setCompareLoading(false);
            }
        }
    };

    const resetCompare = () => {
        compareControllerRef.current?.abort();
        compareControllerRef.current = null;

        setCompareResult(null);
        setCompareError('');
        setCompareLoading(false);
    };

    useEffect(() => {
        return () => {
            compareControllerRef.current?.abort();
        };
    }, []);

    return {
        compareResult,
        compareLoading,
        compareError,
        handleCompare,
        resetCompare
    };
}